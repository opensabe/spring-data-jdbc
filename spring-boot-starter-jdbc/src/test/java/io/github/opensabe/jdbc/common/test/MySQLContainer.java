package io.github.opensabe.jdbc.common.test;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MySQLContainer extends GenericContainer<MySQLContainer> {

    public MySQLContainer() {
        super("mysql");
    }

    protected void configure() {
        this.withEnv("MYSQL_ROOT_PASSWORD", "123456");
        this.withExposedPorts(3306);
    }

    public MySQLContainer withFixedExposedPort(int hostPort, int containerPort) {
        super.addFixedExposedPort(hostPort, containerPort);
        return this;
    }

    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] init = resolver.getResources("classpath*:init*.sql");
            Resource[] data = resolver.getResources("classpath*:data*.sql");
            this.executeSql(init);
            this.executeSql(data);
            System.out.println("MySQL started at port: " + this.getMysqlPort());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void executeSql(Resource[] resources) {

        if (resources != null && resources.length != 0) {
            ExecutorService executorService = Executors.newFixedThreadPool(resources.length);
            List<Future<?>> futures = new ArrayList<>();

            for(Resource resource : resources) {
                futures.add(executorService.submit(() -> {
                    try {
                        String content = resource.getContentAsString(Charset.defaultCharset());
                        Container.ExecResult mysql = null;
                        content = content.replace("\r\n", "\n");

                        while(mysql == null || mysql.getExitCode() == 1 && (mysql.getStderr().contains("connect to") || mysql.getStderr().contains("Access denied"))) {
                            mysql = this.execInContainer("mysql", "-uroot", "-p123456", "-e", content);
                            System.out.println("-----------------------------------------------------------------------------------------\nMySQL command: " + resource + "\nMySQL init result: " + mysql.getStdout() + "\nMySQL init error: " + mysql.getStderr() + "\nMySQL init exit code: " + mysql.getExitCode());
                            TimeUnit.SECONDS.sleep(3L);
                        }

                        if (mysql.getExitCode() != 0) {
                            throw new RuntimeException("MySQL init failed at " + resource);
                        }
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
            }

            futures.forEach((future) -> {
                try {
                    future.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            executorService.shutdownNow();
        }

    }

    public void stop() {
        super.stop();
        System.out.println("MySQL stopped");
    }

    public int getMysqlPort() {
        return this.getMappedPort(3306);
    }
}

