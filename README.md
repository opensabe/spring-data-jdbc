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

## 动态 SQL

在 `@Query` 注解中支持 MyBatis 风格的 `<if test="...">` 动态 SQL 片段。查询方法中若包含 `<if test=` 标签，框架会自动识别并渲染动态 SQL，再执行常规的 SpEL 参数绑定（`:#{#...}`）。

### 基本用法

```java

import io.github.opensabe.jdbc.core.repository.BaseRepository;
import org.springframework.data.jdbc.repository.query.Query;

public interface UserRepository extends BaseRepository<User, String> {

    @Query("""
        select * from t_user where 1=1
        <if test='#user.id != null'> and id = :#{#user.id} </if>
        <if test='#user.name != null and #user.name != ""'> and name = :#{#user.name} </if>
        """)
    List<User> selectByDynamic(User user);
}

```

当 `user.id` 不为 `null` 时，最终执行的 SQL 类似：

```sql
select * from t_user where 1=1 and id = ?
```

条件不满足时，对应的 `<if>` 片段会被移除，不会出现在最终 SQL 中。

### test 表达式

`test` 属性使用 **SpEL** 表达式，求值上下文与 `@Query` 中的 `:#{#...}` 一致，可直接引用方法参数，例如 `#user.id`、`#name`。

`test` 属性值需使用单引号或双引号包裹：

```java
<if test='#user.id != null'>
<if test="#user.name != null and #user.name != ''">
```

条件求值规则：

- 结果为 `Boolean` 时，直接作为真假判断
- 结果为 `Number` 时，非 0 为真
- 其他类型时，非 `null` 为真

### 嵌套

`<if>` 标签支持嵌套：

```java
@Query("""
    select * from t_user where 1=1
    <if test='#user.name != null'>
      <if test='#user.age != null'> and age = :#{#user.age} </if>
    </if>
    """)
List<User> selectByNested(User user);

@Query("""
            select * from t_user where 1=1 <if test='#ids != null && #ids.size >0'> and id in (:ids) </if>
        """)
List<User> selectByDynamicIn (List<String> ids);
```

### 说明

- 当前仅支持 `<if>` 标签，暂不支持 `<where>`、`<foreach>` 等其他 MyBatis 标签
- 动态 SQL 渲染在 SpEL 参数解析之前执行，因此 `<if>` 片段内的 `:#{#...}` 仅在条件为真时才会参与绑定
- 普通 `@Query`（不含 `<if test=`）不受影响，仍走原有查询逻辑
- 因为spring data参数支持集合类型，因此不需要支持foreach标签
## insertSelective

### 联合主键

**spring data jdbc本身不支持联合主键，因此给多个字段添加@Id注解会报错，我们简单支持了一下**

```java

import com.sabegeek.common.jdbc.repository.api.BaseRepository;

//这里不需要添加@Id注解，即使添加，也只能在一个字段上添加

public record PK(String p1, String p2) {
}

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
- 包含@ReadOnlyProperty注解
- 主键数据类型为Long 或者Integer


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
    
    @Id
    @ReadOnlyProperty
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
