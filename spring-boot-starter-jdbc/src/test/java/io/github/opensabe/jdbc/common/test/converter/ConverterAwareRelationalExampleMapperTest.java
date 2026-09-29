package io.github.opensabe.jdbc.common.test.converter;

import io.github.opensabe.jdbc.autoconfigure.config.GenerateConfiguration;
import io.github.opensabe.jdbc.converter.Converter;
import io.github.opensabe.jdbc.converter.ConverterAwareRelationalExampleMapper;
import io.github.opensabe.jdbc.converter.DefaultValueConversionContext;
import io.github.opensabe.jdbc.converter.InternalConversions;
import io.github.opensabe.jdbc.converter.InternalJdbcConverter;
import io.github.opensabe.jdbc.converter.InternalMappingContext;
import io.github.opensabe.jdbc.converter.InternalPropertyValueConverter;
import io.github.opensabe.jdbc.converter.PropertyValueConversionServiceAccessor;
import io.github.opensabe.jdbc.converter.SpecifyPropertyConverterFactory;
import io.github.opensabe.jdbc.core.executor.PropertyAccessorCustomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.convert.PropertyValueConversionService;
import org.springframework.data.convert.SimplePropertyValueConversions;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jdbc.core.convert.DefaultJdbcTypeFactory;
import org.springframework.data.jdbc.core.convert.JdbcArrayColumns;
import org.springframework.data.jdbc.core.convert.QueryMapper;
import org.springframework.data.jdbc.core.convert.RelationResolver;
import org.springframework.data.relational.core.dialect.MySqlDialect;
import org.springframework.data.relational.core.mapping.DefaultNamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConverterAwareRelationalExampleMapperTest {

    private GenericApplicationContext applicationContext;
    private InternalMappingContext mappingContext;
    private InternalJdbcConverter jdbcConverter;
    private PropertyValueConversionService conversionService;

    @BeforeEach
    void setUp() {
        applicationContext = new GenericApplicationContext();
        applicationContext.registerBean(BankValueConverter.class);
        applicationContext.registerBean(PayloadValueConverter.class);
        applicationContext.refresh();

        SimplePropertyValueConversions propertyConversions = new SimplePropertyValueConversions();
        propertyConversions.setConverterFactory(new SpecifyPropertyConverterFactory(applicationContext));
        propertyConversions.afterPropertiesSet();

        InternalConversions conversions = new InternalConversions(
                MySqlDialect.INSTANCE, List.of(), propertyConversions);
        mappingContext = new InternalMappingContext(DefaultNamingStrategy.INSTANCE);
        mappingContext.setSimpleTypeHolder(conversions.getSimpleTypeHolder());

        conversionService = new PropertyValueConversionService(conversions);
        PropertyAccessorCustomizer customizer = accessor ->
                new PropertyValueConversionServiceAccessor<>(accessor, conversionService);
        jdbcConverter = new InternalJdbcConverter(
                mappingContext,
                mock(RelationResolver.class),
                conversions,
                new DefaultJdbcTypeFactory(
                        mock(JdbcOperations.class), JdbcArrayColumns.DefaultSupport.INSTANCE),
                MySqlDialect.MYSQL_IDENTIFIER_PROCESSING,
                customizer);
    }

    @AfterEach
    void tearDown() {
        applicationContext.close();
    }

    @Test
    @DisplayName("QBE 参数应绑定转换后的整数零")
    void shouldBindConvertedZeroIntoQueryParameter() {
        Account probe = new Account();
        probe.bank = new BankValue(0);

        Query query = mapper().getMappedExample(exampleOf(probe));

        assertThat(mappedParameters(query, Account.class).getValues()).containsValue(0);
    }

    @Test
    @DisplayName("自动配置应注册支持 Converter 的 QBE Mapper")
    void shouldConfigureConverterAwareExampleMapper() {
        assertThat(new GenerateConfiguration()
                .relationalExampleMapper(mappingContext, conversionService))
                .isInstanceOf(ConverterAwareRelationalExampleMapper.class);
    }

    @Test
    @DisplayName("查询映射应使用 Converter 的数据库类型")
    void shouldUseConvertedStoreTypeForQueryMapping() {
        RelationalPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(Account.class);

        assertThat(jdbcConverter.getColumnType(entity.getRequiredPersistentProperty("bank")))
                .isEqualTo(Integer.class);
        assertThat(jdbcConverter.getColumnType(entity.getRequiredPersistentProperty("payload")))
                .isEqualTo(String.class);
    }

    @Test
    @DisplayName("QBE 参数应绑定转换后的字符串")
    void shouldBindStringStoreValue() {
        Account probe = new Account();
        probe.payload = new Payload("payload-value");

        Query query = mapper().getMappedExample(exampleOf(probe));

        assertThat(mappedParameters(query, Account.class).getValues())
                .containsValue("payload-value");
    }

    @Test
    @DisplayName("值转换器应先于属性 Converter 执行")
    void shouldApplyValueTransformerBeforePropertyConverter() {
        Account probe = new Account();
        probe.bank = new BankValue(0);
        Example<Account> example = Example.of(
                probe,
                ExampleMatcher.matching()
                        .withIgnoreNullValues()
                        .withTransformer("bank", value -> value.map(ignored -> new BankValue(1))));

        Query query = mapper().getMappedExample(example);

        assertThat(mappedParameters(query, Account.class).getValues()).containsValue(1);
    }

    @Test
    @DisplayName("全条件匹配应以 AND 组合普通字段和 Converter 字段")
    void shouldCombineNormalAndConverterPropertiesWithAnd() {
        Account probe = new Account();
        probe.userId = "user-1";
        probe.bank = new BankValue(0);

        CriteriaDefinition criteria = mapper().getMappedExample(exampleOf(probe))
                .getCriteria().orElseThrow();

        assertThat(criteria.getCombinator()).isEqualTo(CriteriaDefinition.Combinator.AND);
        assertThat(mappedParameters(Query.query(criteria), Account.class).getValues())
                .containsValues("user-1", 0);
    }

    @Test
    @DisplayName("任一条件匹配应以 OR 组合普通字段和 Converter 字段")
    void shouldCombineNormalAndConverterPropertiesWithOr() {
        Account probe = new Account();
        probe.userId = "user-1";
        probe.bank = new BankValue(0);
        Example<Account> example = Example.of(
                probe, ExampleMatcher.matchingAny().withIgnoreNullValues());

        CriteriaDefinition criteria = mapper().getMappedExample(example).getCriteria().orElseThrow();

        assertThat(criteria.getCombinator()).isEqualTo(CriteriaDefinition.Combinator.OR);
    }

    @Test
    @DisplayName("被忽略的 Converter 字段不应进入 QBE")
    void shouldHonorIgnoredConverterPath() {
        Account probe = new Account();
        probe.bank = new BankValue(0);
        Example<Account> example = Example.of(
                probe,
                ExampleMatcher.matching()
                        .withIgnoreNullValues()
                        .withIgnorePaths("bank"));

        Query query = mapper().getMappedExample(example);

        assertThat(query.getCriteria()).hasValueSatisfying(criteria ->
                assertThat(criteria.isEmpty()).isTrue());
    }

    @Test
    @DisplayName("真正的 Reference 字段仍不应进入 QBE")
    void shouldKeepRealReferenceOutOfQueryByExample() {
        Account probe = new Account();
        probe.owner = new Owner("owner-1");

        Query query = mapper().getMappedExample(exampleOf(probe));

        assertThat(query.getCriteria()).hasValueSatisfying(criteria ->
                assertThat(criteria.isEmpty()).isTrue());
    }

    private ConverterAwareRelationalExampleMapper mapper() {
        return new ConverterAwareRelationalExampleMapper(mappingContext, conversionService);
    }

    private Example<Account> exampleOf(Account probe) {
        return Example.of(probe, ExampleMatcher.matching().withIgnoreNullValues());
    }

    private MapSqlParameterSource mappedParameters(Query query, Class<?> entityType) {
        RelationalPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityType);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        org.springframework.data.relational.core.sql.Table table =
                org.springframework.data.relational.core.sql.Table.create(entity.getTableName());
        new QueryMapper(MySqlDialect.INSTANCE, jdbcConverter).getMappedObject(
                parameters,
                query.getCriteria().orElseThrow(),
                table,
                entity);
        return parameters;
    }

    @Table("t_account")
    static class Account {
        @Id
        Long id;
        String userId;
        @Converter(BankValueConverter.class)
        BankValue bank;
        @Converter(PayloadValueConverter.class)
        Payload payload;
        @Reference
        Owner owner;
    }

    record BankValue(Integer id) {
    }

    record Payload(String value) {
    }

    record Owner(String id) {
    }

    public static class BankValueConverter
            implements InternalPropertyValueConverter<BankValue, Integer> {

        @Override
        public BankValue read(Integer value, DefaultValueConversionContext<?> context) {
            return new BankValue(value);
        }

        @Override
        public Integer write(BankValue value, DefaultValueConversionContext<?> context) {
            return value.id();
        }
    }

    public static class PayloadValueConverter
            implements InternalPropertyValueConverter<Payload, String> {

        @Override
        public Payload read(String value, DefaultValueConversionContext<?> context) {
            return new Payload(value);
        }

        @Override
        public String write(Payload value, DefaultValueConversionContext<?> context) {
            return value.value();
        }
    }
}
