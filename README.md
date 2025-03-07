## Feature

1. json字段保存到s3，dynamdb
2. 多数据源查从库s
3. 优化代码，简化Repository实现
4. 打印SQL参数


## 类型转换依赖关系，从上往下依赖

```java

/**
 * 
 * @see org.springframework.data.jdbc.core.convert.JdbcConverter 
 * @see org.springframework.data.mapping.model.ConvertingPropertyAccessor
 * 
 * @see org.springframework.core.convert.ConversionService
 * @see org.springframework.data.convert.PropertyValueConversionService
 * 
 * @see org.springframework.data.convert.CustomConversions
 * 
 * @see org.springframework.data.convert.PropertyValueConversions
 * 
 * @see org.springframework.data.convert.PropertyValueConverterFactory
 * 
 * @see org.springframework.core.convert.converter.GenericConverter
 */


```

## insertSelective

### 联合主键

**spring data jdbc本身不支持联合主键，因此给多个字段添加@Id注解会报错，我们简单支持了一下**

```java

import com.sabegeek.common.jdbc.repository.api.BaseRepository;

//这里不需要添加@Id注解，即使添加，也只能在一个字段上添加

public record PK(String p1, String p2) {
}

;

public class User extends PK {

    private String name;

    User(String p1, String p2, String name) {
        super(p1, p2);
        this.name = name;
    }
}

public interface UserRepository extends BaseRepository<User, PK> {

}

private UserRepository repository;

void test() {
    repository.insertSelective(new User("p1", "p2", "n1"));
}
```

### 自增主键

**这里需要注意一下，spring data jdbc没有在实体类上标识是否是自增主键，而是在insert的过程中自动判断，判断的标准：**

- 包含有@Id注解
- 主键数据类型为Long 或者Integer
- 实体上主键为空或者为0

**这三个条件都满足才会被判定为自增主键**

```mysql-sql
create table sys.t_role (
    id int auto_increment,
    `name` varchar(12),
    primary key(id)
);
```

```java

import com.sabegeek.common.jdbc.repository.api.BaseRepository;
import org.springframework.util.Assert;

public class Role {
    private Integer id;
    private String name;

    //忽略其他get set方法

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

public class RoleRepository extends BaseRepository<Role, Integer> {
}

void test() {
    RoleRepository repository;

    Role role = new Role();
    role.setName("n1");

    repository.insertSelective(role);

    Assert.notNull(role.getId(), "id is not generated");

}

```

### 自定义转换器

1. 编写converter
   - 需要实现 PropertyValueConverter接口，为了方便我们写泛型，我写了一个InternalPropertyValueConverter接口，
   该接口继承了PropertyValueConverter，因此，我们在使用时，直接实现InternalPropertyValueConverter即可

   - 自定义的converter必须是一个spring对象，因为在获取转换器时使用的是ApplicationContext


```java

import org.springframework.stereotype.Component;

@Component
public class JsonPropertyValueConverter implements InternalPropertyValueConverter<Object, String> {


    @Override
    public Object read(String value, DefaultValueConversionContext context) {
        TypeInformation typeInformation = context.getProperty().getTypeInformation();
        return JsonUtil.parseObject(value, JacksonParameterizedTypeTypeReference.fromTypeInformation(typeInformation));
    }

    @Override
    public String write(Object value, DefaultValueConversionContext context) {
        return JsonUtil.toJSONString(value);
    }
}

```
2. 在实体类升添加注解 @Converter，并指定转换器

```java

public class Activity {

    @Id
    private String id;

    @Converter(JsonPropertyValueConverter.class)
    private Config config;

    @Converter(JsonPropertyValueConverter.class)
    private List<String> platforms;

    @Converter(JsonPropertyValueConverter.class)
    private Map<String, Boolean> times;

    private Boolean online;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        private String key;
        private String value;
    }
}

```
