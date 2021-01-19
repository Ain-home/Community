#### SP论坛系统开发笔记

#### 前期准备

**Apache Maven**

可以构建项目、管理项目中的 jar 包

Maven 仓库：存放构件的位置

- 本地仓库：默认是 ~/.m2/repository
- 远程仓库：中央仓库、镜像仓库、私服仓库

常用命令：

- mvn -version   查看maven版本

具体见maven官网

**Spring Initializr**

创建 Spring Boot 项目的引导工具

Spring Boot 核心作用：起步依赖，自动配置，端点监控。

application.properties配置:

server.servlet.context-path=/community     设置项目的访问路径

**Spring**

- Spring Core(Spring核心) 包括 IOC,AOP
- Spring Data Access(Spring访问数据库) 包括 Transactions,MyBatis
- Web Servlet(Web开发) 包括 Spring MVC
- Integration(集成) 包括 Email,Scheduling(定时任务),AMQP(消息队列),Security(安全控制)

1.获取Ioc容器（SpringBoot中），通过指定类型或者bean的ID（@Qualifier("id")）获取bean

2.设置bean的优先级 @Primary 注解（同种类型的bean时该bean优先装配）

3.ioc容器管理bean的初始化和销毁 @PostConstruct（调用构造方法后调用初始化方法） 和 @PreDestroy（在销毁对象之前调用销毁方法） 注解

**Spring MVC**

- Http 协议：HyperText Transfer Protocol 用于传输HTML等内容的应用层协议，规定了浏览器和服务器之间如何通信以及通信时的数据格式

  更多见 https://developer.mozilla.org/zh-CN/

- 三层架构：表现层，业务层，数据访问层

- MVC（设计模式）：Model 模型层（封装数据），View 视图层（渲染展示），Controller 控制层（调度，处理请求）

- Spring mvc核心组件：前端控制器 DispatcherServlet（基于ioc容器全局调度controller,viewResolver,HandlerMapping）

![image-20201205150321415](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201205150321415.png)

1.获取request和response对象（原始）：在controller中向需要request、response的方法传入HttpServletRequest、HttpServletResponse参数即可

2.@RequestParm 指定请求参数的设置，获取请求地址格式如：/student?id=1  使用问号拼接传入参数

3.@PathVariable 指定请求参数，获取请求地址格式如：/student/1  (/student/{id}) 

4.异步请求响应JSON数据（局部）：将Java对象转换为JS对象（JSON字符串），例如返回Map类型对象（属性和属性值）

**Thymeleaf** 

- 模板引擎  生成动态的HTML
- Thymeleaf 倡导自然模板，即以HTML文件为模板
- 常用语法：标准表达式，判断与循环，模板的布局

![image-20201205151730821](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201205151730821.png)

1.Thymeleaf缓存：开发时关闭，上线时打开（降低服务器压力）  spring.thymeleaf.cache = false （关闭）

**SpringBoot**

application.properties配置文件实现自动配置原理：

比如 server.port = 8080 配置此应用程序的端口，是注入数据到 ServerProperties 配置类中（和bean类似的方式），其他等等都一样，如 ThymeleafProperties 类配置 thymeleaf 模板引擎参数 spring.thymeleaf.cache = false

**MyBatis**

1.mybatis的核心组件：

- SqlSessionFactory 用于创建SqlSession的工厂类

- SqlSession mybatis的核心组件，用于向数据库执行SQL

- 主配置文件：XML配置文件，可以对mybatis的底层行为作出详细的配置

- Mapper接口：就是DAO接口，在mybatis中习惯性称为Mapper，使用@Mapper注解

  <u>@Mapper和@Repository的区别</u>：

  1）使用@mapper后，不需要在spring配置中设置扫描地址，通过mapper.xml里面的namespace属性对应相关的mapper类，spring将动态的生成Bean后注入到ServiceImpl中

  2）@Repository则需要在Spring中配置扫描包地址，然后生成dao层的bean，之后被注入到ServiceImpl中

- Mapper映射器：用于编写SQL，并将SQL和实体类映射的组件，采用XML、注解均可实现

  主键自动生成并注入到实体类中：xml  设置属性  keyProperty="id"   注解  @Options(useGeneratedKeys=true,keyProperty = "id")

2.springboot中mybatis的配置：

- mybatis.mapper-locations = classpath:mapper/*.xml   意为类路径下mapper目录下的所有xml文件
- mybatis.type-aliases-package = com.nowcoder.community.entity  意为对应实体类所在的包
- mybatis.configuration.useGeneratedKeys = true 启用自动生成主键（自增长字段）
- mybatis.configuration.mapUnderscoreToCamelCase = true 自动适应驼峰式命名（字段表名等等）

3.DAO使用mybatis时便于调试，调整dao层的日志级别 logger.level.com.nowcoder.community.dao = debug

**项目调试技巧**

1.响应状态码的含义

Http状态返回码，见 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status

常见的状态码有 200（OK）,302（重定向），404（Not Found，路径错误）,500（服务器接收到请求，但服务端处理过程出错）

2.服务端断点调试技巧

F7 进入当前行调用的方法内部,F8 程序逐行运行,F9 程序直接向下执行，直到下一个断点位置

3.客户端端点调试技巧

在浏览器中的检查，调试 JS 代码，同样是可以使用断点

F10  程序逐行运行，F11 进入当前行调用的方法内部，F8 执行到底或下一个断点

4.设置日志级别，并将日志输出到不同的终端

slf4j日志

日志级别（由低到高）：trace,debug,info,warn,error

在配置文件application中设置日志级别，默认为info

可以简单地在application中设置日志文件的存放路径，但实际开发中日志比较重要，甚至还可以需要按照日志级别存放在不同的文件中，所以可能需要更复杂的设置

**版本控制**

Git 见 https://git-scm.com/book/zh/v2 分布式版本控制

Git CMD是window下的命令行，Git Bash 是linux风格的命令行

Git常用命令：

Git CMD下

配置用户名和邮箱：

```bash
git config --global user.name "username"
git config --global user.email "email@email"
git config --list   //列出所有配置信息
```

cd到需要 git 管理的项目目录下，提交保存到本地仓库：

```bash
git init   //初始化git
git status   //当前项目git管理的状态
//初始化后所有目录为红色的
git add *   //添加当前项目下的所有文件临时存到本地仓库，还没有提交（提交后才能在本地仓库永久保存）
//添加后 status 为绿色的
git commit -m "备注"  //提交  -m ""为此次提交添加备注
//提交后 status 为空，只有当代码有改变时，才能出现再次提交的红色文件（说明本次修改的文件还没有添加提交到本地仓库中）
```

git 将代码传输到远程仓库：

```bash
ssh-keygen -t rsa -C "email"   //生成密钥SSH key
```

生成密钥后需要到远程仓库中配置，添加自己这里生成的密钥

然后在远程仓库中新建项目（保存要上传的项目）

```bash
git remote add 别名 远程仓库路径.git   //在本地声明一个远程仓库，起别名替代复杂的原名称
git push -u 别名 master(分支)  //将当前项目的代码上传到远程仓库的xx分支中
```

git 从远程仓库中复制项目到本地仓库：

首先从远程仓库获取HTTPS或SSH路径（都可以）

想要放到本地什么目录下，先cd到准备存放从远程仓库复制的项目文件夹中：

```bash
git clone https/ssh路径
```

项目就放到本地该目录下了。

IDEA集成Git：

配置Git

setting -> Version Control -> Git -> Path to Git executable : 设置为本地Git的安装程序Git.exe -> Apply

添加项目到本地仓库（该项目会生成隐藏文件 .git）

VCS -> Import into Version Control -> create Git Repository  (文件名都变为红色)

提交项目到本地仓库

VCS -> Commit/Git -> Commit Directory -> 选择提交到本地仓库的文件 -> Commit Message 编辑提交的备注信息 -> Commit （提交的文件名都变为白色）

提交到远程仓库

VCS -> Git -> Push(Pull是下载新的项目文件到本地) -> Define remote (定义一个远程仓库) -> OK 之后右侧列出了本地仓库已经提交的代码 -> Push -> 输入远程仓库的账号密码 -> Log In -> Push Successful

#### SpringBoot实践：社区登录模块

**发送邮件（注册）**

- 邮箱设置（启用客户端SMTP服务）

  在选择的邮箱开启POP3/STMP服务，生成授权码

- Spring Email （导入jar包，邮箱参数配置，使用 JavaMailSender 发送邮件）

  jar: spring-boot-starter-mail

  邮箱参数配置：application.yml  spring.mail...

  使用核心 JavaMailSender 构建发送邮件的工具类

- 模板引擎 （使用 thymeleaf 发送 HTML 邮件）

  编写邮件模板

**注册功能**

- 注册页面

- 提交注册数据

  通过表单提交注册数据

  服务端验证账号是否已存在、邮箱是否已注册

  服务端发送激活邮件

- 激活注册账号

  点击邮件中的链接，访问服务端的激活服务

**会话管理**

- HTTP的基本性质

  HTTP是简单的；

  HTTP是可扩展的；

  HTTP是无状态的，有会话的

  ![image-20201210225522992](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201210225522992.png)

- Cookie

  cookie是服务器发送到浏览器，并保存在浏览器端的一小块数据；

  浏览器下次访问该服务器时，会自动携带该块数据，将其发送给服务器；

  通常，它用于告知服务端两个请求是否来自同一浏览器

  Spring mvc中 @CookieValue("key")  获取指定key的cookie的值（使用在方法参数前）

  Cookie保存敏感信息不安全

- Session

  session是JavaEE的标准，用于在服务端记录客户端信息；

  数据存放在服务端更加安全，但是相应的也会增加服务端的内存压力

  session依赖于cookie，session由服务端保存，但它会通过响应给浏览器发送一个保存着JSESSIONID为key的cookie由浏览器保存，下次访问时服务端通过这个cookie识别浏览器

  HttpSession由Spring mvc自动注入，只需要声明使用即可（不同于cookie还需要借助response创建）

  分布式中Session使用的少。因为在分布式系统中，存在多台服务器(集群)，由nginx(代理)调度负载均衡，分发请求给服务器，当一个session保存在一个服务器时，其他的服务器难以共享这个session(虽然有方法，但并不完美).

  **服务器集群共享session**:

  - 粘性session（固定ip固定服务器处理，负载难以均衡）
  - 同步session（当一个服务器创建session时其他服务器也创建同样的session，同步session影响服务器性能，且会让服务器之间存在耦合）
  - 共享session（由一台独立的服务器保存所有的session，但当这台服务器崩了，会影响所有的服务器）

  <u>目前一般将session信息保存到数据库，但传统的数据库数据保存在硬盘，与从内存中直接读取数据相比性能较差，所以存到nosql（Redis）是最好的解决方法。</u>

**生成验证码**

- Kaptcha

  使用见 https://code.google.com/archive/p/kaptcha 的Wikis

  导入jar包 -> 编写Kaptcha配置类 -> 生成随机字符、生成图片

**登录、退出登录**

- 访问登录页面
- 登录 -- 验证账号，密码（敏感信息先存放到数据库中，将来重构放到redis中），验证码，成功时，生成登录凭证，发放给客户端；失败时，返回登录页面
- 退出 -- 将登录凭证修改为失效状态，跳转至网站首页（未登录状态）

**显示登录信息**

- 使用拦截器

  定义拦截器，实现HandlerInterceptor

  - preHandle()  在请求（Controller）前执行 
  - postHandle()  在请求（controller）后执行
  - afterCompletion()  在templateEngine模板引擎之后执行

  配置拦截器，为它指定拦截、排除的路径

  - 配置类实现 WebMvcConfigurer 接口

  - 注入拦截器

  - 实现 addInterceptors(InterceptorRegistry registry) 方法

  - InterceptorRegistry 拦截器注册器，

    注册哪些拦截器（addInterceptor）【一般为拦截器】；

    哪些不需要拦截（excludePathPatterns）【一般为静态资源】；

    哪些拦截路径（addPathPatterns）

- 拦截器应用：每次请求都需要反复去做的

  在请求开始时查询登录用户；

  在本次请求中持有用户数据；

  在模板视图上显示用户数据；

  在请求结束时清理用户数据。

**账号信息设置**

- 上传文件（更换头像）
  - 请求：必须是POST请求
  - 表单：`enctype="multipart/form-data"`
  - SpringMvc：通过 MultipartFile 处理上传文件
- 开发步骤
  - 访问账号设置页面
  - 上传头像（图片可以存在本地服务器上，也可以存在第三方的云服务器）
  - 获取新头像（更新用户头像）
  - 修改密码，用户其他信息

**检查登录状态**

如果用户能记住我们定义隐藏访问的页面，则可以直接通过输入地址访问该页面（比如用户未登录，就可以通过地址访问账号设置页面），显然这是不允许的，是bug，是安全隐患。很多地方都需要解决这样的问题，可以使用拦截器（习惯上通过注解标注要拦截的方法）：

- 使用拦截器

  - 在方法前标注自定义注解
  - 拦截所有请求，只处理带有该注解的方法

- 自定义注解

  - 常见的元注解：

    @Target（声明自定义的注解可以写在哪些位置，可以作用在哪些区域）；

    @Retention（自定义注解保留的时间，有效时间，编译时有效，还是运行时有效等等）；

    @Document（声明在生成文档时是否需要加上该自定义注解）；

    @Inherited（用于继承，当父类上有自定义注解时，子类是否需要继承过来该注解）

    自定义注解 前两个注解是一定要用的

  - 如何读取注解（通过反射）：

    `Method.getDeclaredAnnotations()` 获取该方法上的所有注解

    `Method.getAnnotation(Class<T> annotationClass)` 尝试获取某种类型的注解

#### SpringBoot实践：社区核心功能

**过滤敏感词**

- 前缀树：
  - 名称：Trie、字典树、查找数
  - 特点：查找效率高、消耗内存大（空间换时间）
  - 应用：字符串检索、词频统计、字符串排序等
- 敏感词过滤器（算法）：
  - 定义前缀树
  - 根据敏感词，初始化前缀树（只有到遍历叶子节点才是一个完整的敏感词）
  - 编写过滤敏感词的方法（敏感词用*代替）

![image-20201216215912330](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201216215912330.png)

![image-20201216220054090](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201216220054090.png)

- 步骤：
  - 1.定义敏感词集sensitive-word.txt（每个敏感词独占一行）
  - 2.定义前缀树
  - 3.初始化前缀树，在容器加载的时候就初始化
  - 4.实现过滤算法

**发布帖子**

- AJAX：异步通讯技术
  - Asynchronous JavaScript and XML
  - 异步的JavaScript和XML，不是新技术，只是一个新术语
  - 使用AJAX，网页能够将增量更新呈现在页面上，而不需要刷新整个页面
  - 虽然X代表XML，但目前JSON的使用比XML更加普遍
- 实践：采用AJAX请求，实现发布帖子的功能

**帖子详情**

- index.html 在帖子标题上添加访问详情页面的链接
- discuss-detail.html 显示标题、作者、发布时间、帖子正文等等内容

**事务管理**

- 什么是事务

  事务是由N步数据库操作序列组成的逻辑执行单元（业务），这系列操作要么全执行，要么全放弃执行

- 事务的特性（ACID）

  - 原子性（Atomicity）：事务是应用中不可再分的最小执行体

  - 一致性（Consistency）：事务执行的结果，须使数据从一个一致性状态，变为另一个一致性状态（没有改变之前满足约束，改变之后依然满足）

  - 隔离性（Isolation）：各个事务的执行互不干扰，任何事务的内部操作对其他的事务都是隔离的

  - 持久性（Durability）：事务一旦提交，对数据所做的任何改变都要记录到永久存储器中

- 事务的隔离性

  - 常见的并发异常：读，取

    - 第一类丢失更新、第二类丢失更新
  
      第一类丢失更新：某一个事务的回滚，导致另外一个事务已更新的数据丢失了
  
      ![image-20201221192131815](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201221192131815.png)
  
      第二类丢失更新：某一个事务的提交，导致另一个事务已更新的数据丢失了
  
      ![image-20201221192404524](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201221192404524.png)
  
    - 脏读、不可重复读、幻读
  
      脏读：某一个事务，读取了另外一个事务未提交的数据
  
      ![image-20201221192542310](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201221192542310.png)
  
      不可重复读：某一个事务，对同一个数据前后（短时间间隔内）读取的结果不一致（前后读取的不同结果参与运算矛盾了）
  
      ![image-20201221192927482](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201221192927482.png)
  
      幻读：某一个事务，对同一个表前后查询到的行数（查询的是多条数据）不一致
  
      ![image-20201221193127852](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201221193127852.png)
  
      
  
  - 常见的隔离级别（由低到高）
  
    - Read Uncommitted：读取未提交的数据
    - Read Commited：读取已提交的数据
    - Repeatable Read：可重复读
    - Serializable：串行化（需要加锁，效率低）
  
    ![image-20201221193427114](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201221193427114.png)

- 数据库保障事务的机制--实现机制

  - 悲观锁（数据库自带）：如果并发就一定会有问题

    - 共享锁（S锁）

      事务A对某数据加了共享锁后，其他事务只能对该数据加共享锁，但不能加排他锁

    - 排他锁（X锁）

      事务A对某数据加了排他锁后，其他事务对该数据既不能加共享锁，也不能加排他锁

  - 乐观锁（自定义）：认为即使并发了也不会有问题

    - 添加版本号、时间戳等

      在更新数据前，检查版本号是否发生变化。若变化则取消本次更新，否则就更新数据（版本号+1）

- Spring事务管理

  - 声明式事务
    - 通过XML配置，声明某方法的事务特征
    - 通过注解，声明某方法的事务特征
  - 编程式事务（方法局部事务管理）
    - 通过 TransactionTemplate 管理事务，并通过它执行数据库的操作

- 事务的传播机制

  解决事务交叉处理的问题，以哪个事务为基准

  Propagation.REQUIRED：支持当前事务（A事务调用B事务，当前事务为A），若不存在则创建新事务

  Propagation.REQUIRED_NEW：创建一个新的事务，并且暂停当前事务

  Propagation.NESTED：若当前存在事务，则嵌套在当前事务中执行，否则就和REQUIRED一样

**显示评论**

- 数据层
  - 根据实体查询一页评论数据
  - 根据实体查询评论的数量
- 业务层
  - 处理查询评论的业务
  - 处理查询评论数量的业务
- 表现层
  - 显示帖子详情数据时，同时显示该帖子所有的评论数据

**添加评论**

- 数据层

  - 增加评论数据
  - 修改帖子的评论数量

- 业务层

  - 处理添加评论的业务

    先增加评论，再更新帖子的评论数量（事务管理）

- 表现层

  - 处理添加评论数据的请求
  - 设置添加评论的表单

**私信列表** 

- 私信列表
  - 查询当前用户的会话列表，每个会话只显示一条最新的私信信息
  - 支持分页显示
- 私信详情
  - 查询某个会话所包含的私信
  - 支持分页显示

**发送私信**

- 发送私信
  - 采用异步的方式发送私信
  - 发送成功后刷新私信列表
- 设置已读
  - 访问私信详情时，将显示的私信设置为已读状态

**统一处理异常**

- @ControllerAdvice
  - 用于修饰类，表示该类是Controller的全局配置类
  - 在此类中，可以对Controller进行如下三种全局配置：异常处理方案、绑定数据方案、绑定参数方案
- @ExceptionHandler
  - 用于修饰方法，该方法会在Controller出现异常后调用，用于处理捕获到的异常
- @ModelAttribute
  - 用于修饰方法，该方法会在Controller方法执行被调用，用于为Model对象绑定参数
- @DataBinder
  - 用于修饰方法，该方法会在Controller方法执行前被调用，用于绑定参数的转换器

**统一记录日志**

- AOP （Aspect Oriented Programing）

  面向方面（切面）编程

- AOP是一种编程思想，是对OOP的补充，可以进一步提高编程的效率

![image-20201228215000784](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201228215000784.png)

![image-20201228215546612](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20201228215546612.png)

- AOP的实现
  - AspectJ
    - AspectJ 是语言级的实现，它扩展了Java语言，定义了AOP语法
    - AspectJ 在编译期织入代码，它有一个专门的编译器，用来生成遵守Java字节码规范的class文件
  - Spring AOP
    - Spring AOP 使用纯Java实现，它不需要专门的编译过程，也不需要特殊的类加载器
    - Spring AOP 在运行时通过代理的方式织入代码，只支持方法类型的连接点
    - Spring 支持对 AspectJ 的集成
- Spring AOP
  - JDK动态代理
    - Java提供的动态代理技术，可以在运行时创建接口的代理实例
    - Spring AOP默认采用此种方式，在接口的代理实例中织入代码
  - CGLib动态代理
    - 采用底层的字节码技术，在运行时创建子类代理实例
    - 当目标对象不存在接口时，Spring AOP会采用此种方式，在子类实例中织入代码

#### Redis--一站式高性能存储方案

- Redis入门

  - Redis是一款基于键值对的NoSQL数据库，它的值（key都是string）支持多种数据结构：字符串、哈希、列表、集合、有序集合[score]等
  - Redis将所有的数据都存放在内存中，所以它的读写性能十分惊人；同时，Redis还可以将内存中的数据以快照(rdd)或日志(aof)的形式保存到硬盘上，以保证数据的安全性
  - Redis典型的应用场景包括：缓存、排行榜、计数器(浏览量)、社交网络(点赞)、消息队列等

  window下载：https://github.com/microsoftarchive/redis

  https://redis.io  官网上只能下载linux系统的

- Redis常用command

  - redis-cli   配置好redis的环境变量后，在命令行连接到redis
  - select index(0-15)   redis内置16个库，默认为0库，通过该命名切换
  - flushdb   刷新库，将库里面的数据清除
  - set key value   添加strings类型的数据
  - get key  获取指定key的value
  - incr key    指定key的value+1
  - decr key   指定key的value-1
  - hset key value   添加哈希类型的数据
  - hget key   获取指定哈希key的value
  - lpush key value   添加列表类型的数据（队列，从左进入）
  - llen key    查看列表的长度
  - lindex key [index]索引   查看列表某个索引上的值
  - lrange key start stop    查看列表某索引段上的值(包含start 和 stop索引上的值)
  - rpop key      表示从右侧出一个值
  - sadd key value[s]   添加集合元素
  - scard key      统计集合中有多少元素
  - spop key     随机从集合中弹出一个元素
  - smembers key    统计集合中还有多少元素
  - zadd key score member   添加有序集合元素
  - zcard key     统计有序集合中有多少元素
  - zscore key member   查询有序集合中某个元素对应的score
  - zrank key memebr   返回有序集合中某个元素的排名（由小到大）
  - zrange key start stop   查看有序集合某段元素的值
  - keys *     查看当前库中存在的key
  - type key   查看某个key对应的value的类型
  - exists key    查看某个key是否存在
  - del key   删除某个key
  - expire key 秒    设置key的过期时间

- Spring 整合 Redis
  - 引入依赖  spring-boot-starter-data-redis
  - 配置redis
    - 配置数据库参数
    - 编写配置类，构造RedisTemplate
  - 访问Redis
    - redisTemplate.opsForValue()
    - redisTemplate.opsForHash()
    - redisTemplate.opsForList()
    - redisTemplate.opsForSet()
    - redisTemplate.opsForZSet()

**点赞**

- 点赞
  - 支持对帖子、评论点赞
  - 第一次点赞，第二次点击取消点赞
- 首页点赞数量
  - 统计帖子的点赞数量
- 详情页点赞数量
  - 统计点赞数量
  - 显示点赞状态

**我收到的赞（用户获得的总赞数【包括帖子和评论】）**

- 重构点赞功能
  - 以用户为key，记录点赞数量
  - increment(key)，decrement(key)
- 开发个人主页
  - 以用户为key，查询点赞数量

**关注、取消关注**

- 需求
  - 开发关注、取消关注功能
  - 统计用户的关注数、粉丝数
- 关键
  - 若A关注了B，则A是B的Follower(粉丝)，B是A的Followee(目标)
  - 关注的目标可以是用户、帖子、题目等，在实现时将这些目标抽象为实体

**关注列表、粉丝列表**

- 业务层
  - 查询某个用户关注的人，支持分页
  - 查询某个用户的粉丝，支持分页
- 表现层
  - 处理"查询关注的人"、"查询粉丝"请求
  - 编写"查询关注的人"、"查询粉丝"模板

**优化登录模块**

- 使用Redis存储验证码
  - 验证码需要频繁的访问与刷新，对性能要求较高
  - 验证码不需要永久保存，通常在很短的时间后就会失效
  - 分布式部署时，存在Session共享的问题
- 使用Redis存储登录凭证
  - 处理每次请求时，都要查询用户的登录凭证，访问的频率非常高
- 使用Redis缓存用户信息
  - 处理每次请求时，都要根据凭证查询用户信息，访问的频率非常高

#### Kafka，构建TB级异步消息系统

TB级别：数据量非常大

服务器自动给用户发消息（通知），如点赞通知等等

**阻塞队列**【Java自带的】

- BlockingQueue

  ![image-20210103193019870](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210103193019870.png)

  - 解决线程通信的问题
  - 阻塞方法：put、take

- 生产者消费者模式

  - 生产者：产生数据的线程
  - 消费者：使用数据的线程

- 实现类

  - ArrayBlockingQueue
  - LinkedBlockingQueue
  - PriorityBlockingQueue、SynchronousQueue、DelayQueue等

**Kafka入门**

- kafka简介
  - kafka是一个分布式的流媒体平台
  - 应用：消息系统、日志收集、用户行为追踪、流式处理
  - 官网：http://kafka.apache.org/
- kafka特点
  
  - 高吞吐量、消息持久化【把数据存到硬盘里】、高可靠性【分布式】、高扩展性
- kafka术语
  - Broker【服务器】、Zookeeper【管理集群】
  - Topic【主题】、Partition【分区】、Offset【索引】
  - Leader Replica【主副本】、Follower Replica【从副本】

- 下载安装kafka

  - 官网下载解压缩即可
  - 配置 zookeeper.properties  修改数据存放路径为window下的路径
  - 配置 server.properties 修改日志存放路径为window下的路径

- Kafka命令

  要先启动zookeeper【kafka依赖于zookeeper】，进入到kafka的目录下

  - ```bin\windows\zookeeper-server-start.bat config\zookeeper.properties```

    以 zookeeper.properties 配置文件启动zookeeper

  再重新开启一个命令行，同样进入到kafka目录下

  - ```bin\windows\kafka-server-start.bat config\server.properties```

    以 server.properties 配置文件启动kafka

  - ```kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test```

    创建主题【topic】，一个副本一个分区名为test的主题

  - ```kafka-topics.bat --list --bootstrap-server localhost:9092```

    查看9092服务器中的主题

  - ```kafka-console-producer.bat --broker-list localhost:9092 --topic test```

    以生产者模式的身份向9092服务器的test主题发送消息【在出现的命令行下可以向消费者模式发送消息】

  - ```kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic test --from-beginning```

    以消费者模式的身份从9092服务器的test主题从头到尾读消息

**Spring整合Kafka**

- 引入依赖

  - spring-kafka

- 配置kafka

  - 配置server、consumer

- 访问Kafka

  - 生产者

    kafkaTemplate.send(topic, data);

  - 消费者

    @KafkaListener(topics = {"test"})

    public void handleMessage(ConsumerRecord record)

**发送系统通知**

- 触发事件
  - 评论后，发布通知
  - 点赞后，发布通知
  - 关注后，发布通知
- 处理事件（事件驱动）
  - 封装事件对象
  - 开发事件的生产者
  - 开发事件的消费者

![image-20210104181245526](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210104181245526.png)

发送消息和接受消息异步进行

**显示系统通知**

- 通知列表
  - 显示评论、点赞、关注三种类型的通知
- 通知详情
  - 分页显示某一类主题所包含的通知
- 未读消息
  - 在页面头部显示所有未读消息数量

#### Elasticsearch,分布式搜索引擎

**Elasticsearch入门**

- Elasticsearch简介
  - 一个分布式的、Restful风格的搜索引擎
  - 支持对各种类型的数据的检索
  - 搜索速度快，可以提供实时的搜索服务
  - 便于水平扩展，每秒可以处理PB级海量数据
- Elasticsearch术语
  - 索引、类型、文档、字段
  - 集群、节点、分片、副本

官网：https://www.elastic.co/cn/

注意：下载的ElasticSearcher的版本必须和springboot版本相契合

2.4.0  --  7.9.3

修改config文件夹下的elasticsearch.yml配置文件

配置环境变量Path  bin

安装中文分词插件（ElasticSearch默认是英文分词）

https://github.com/medcl/elasticsearch-analysis-ik

第三方中文分词插件 ik

解压到 elasticsearch 的 plugins 目录下的 ik 文件夹中

配置 ik config 目录下的 IKAnalyzer.cfg 可以新增一些默认自带的词典中没有的新词（创建自己的dic，映射过去）

启动  bin 目录下的 elasticsearch.bat   默认占用9200端口

**常用命令**：

查看elasticsearcher集群的健康状态：

![image-20210105210147785](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105210147785.png)

查看集群中的节点

![image-20210105210454674](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105210454674.png)

查看索引（该结果表示没有索引）

![image-20210105210616426](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105210616426.png)

创建索引（创建名为test的索引）-返回结果是JSON格式的

![image-20210105210740371](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105210740371.png)

由于没有设置分区等等（不健康的索引）

![image-20210105211023855](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105211023855.png)

删除索引

![image-20210105211048208](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105211048208.png)

**使用Postman代替命令行对elasticsearch进行操作**

![image-20210105211358823](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105211358823.png)

![image-20210105211509904](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105211509904.png)

提交数据（改数据只需要在JSON字符中修改【底层会自动进行删除新增操作】）

![image-20210105212117576](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105212117576.png)

查数据

![image-20210105212235984](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105212235984.png)

删数据

![image-20210105212428882](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105212428882.png)

实现搜索

简单条件搜索

![image-20210105213552375](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105213552375.png)

复杂条件搜索

![image-20210105213958262](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210105213958262.png)

**Spring整合Elasticsearch**

- 引入依赖
  
  - spring-boot-starter-data-elasticsearch
  
- 配置Elasticsearch
  - cluster-name   集群的名字
  - cluster-nodes  集群的节点
  
  7.X 版本需要使用配置类配置 RestHighLevelClient
  
  注意：同时使用redis时，需要解决Netty启动冲突
  
- Spring Data Elasticsearch
  - ElasticsearchTemplate
  
  - ElasticsearchRepository
  
    配置实体类，索引--表一一对应

9200是http访问端口，9300是tcp访问端口

将帖子存到es服务器中，使其能够被检索到

**开发社区搜索功能**

- 搜索服务（业务）
  - 将帖子保存到elasticsearch服务器
  - 从elasticsearch服务器删除帖子
  - 从elasticsearch服务器搜索帖子
- 发布事件
  - 发布帖子时，将帖子异步的提交到elasticsearch服务器
  - 增加评论时，将帖子异步的提交到elasticsearch服务器
  - 在消费组件中增减一个方法，消费帖子发布事件
- 显示结果
  - 在控制器中处理搜索请求，在HTML上显示搜索结果

#### 项目进阶，构建安全高效的企业服务

**Spring Security**

- 简介：Spring Security是一个专注于为 Java 应用程序提供身份认证和授权的框架，它的强大之处在于它可以轻松扩展以满足自定义的需求
- 特征
  - 对身份的认证和授权提供全面的、可扩展的支持
  - 防止各种攻击，如会话固定攻击、点击劫持、csrf攻击等
  - 支持与Servlet API、spring mvc等web技术集成

官网：https://spring.io/projects/spring-security

导入了依赖包spring-security立即对项目进行接管（进行权限控制）

对需要进行权限管理的对象（user）进行权限管理：实现接口UserDetails

对需要进行权限管理的对象的业务层（UserService）进行管理：实现接口UserDetailsService

编写配置类继承 WebSecurityConfigurerAdapter父类，重写它的一些方法

**权限控制**

- 登录检查

  之前采用拦截器实现了登录检查，这是较为简单的权限管理方案，现在将其废弃

- 授权配置

  对当前系统内包含的所有请求，分配访问权限（普通用户，版主，管理员）

- 登录认证方案

  绕过security认证流程，采用系统原本的认证方案

- CSRF配置（Token）

  防止CSRF攻击的基本原理，以及表单【自动处理】、AJAX相关【异步处理】的配置
  
  表单都会自动带上隐藏的input传入CSRF令牌
  
  每个异步请求都需要额外进行CSRF令牌的设置
  
  Security自动开启了防止CSRF攻击，可以在授权中关闭

**置顶、加精、删除**

- 功能实现

  - 点击置顶，修改帖子的类型
  - 点击 加精 删除 修改帖子的状态

- 权限管理

  - 版主可以执行 置顶 加精 操作
  - 管理员可以执行 删除 操作

- 按钮显示

  - 版主可以看到 置顶 加精按钮
  - 管理员可以看到 删除 按钮

  注：Thymeleaf支持Security【有一些标签】，需要在html上添加  namaspace

  ```html
  <html xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
  ```

  https://github.com/thymeleaf/thymeleaf-extras-springsecurity

#### Redis高级数据类型

**HyperLogLog  超级日志**

- 采用一种基数算法，用于完成独立总数【比如一天内，一个人多次访问一个网站，计为一个访问，需要对相同访客的重复请求去重统计网站的访问量】的统计
- 占据空间小，无论统计多少个数据，只占12K的内存空间
- 不精确的统计算法，标准误差为0.81%

**Bitmap 位图**

- 不是一种独立的数据结构，实际上就是字符串【特殊格式】
- 支持按位【字符串，每位（索引）只能存0或1，所以是0和1组成的字符串】存取数据，可以将其看出byte数组
- 适合存储大量的连续的数据布尔值

这两种数据类型都适合用于对网站运营的数据进行统计，而且统计时节约内存，效率比较高

redis指令：

```shell
127.0.0.1:6379[11]> info memory
# Memory  查看内存占用详情
used_memory:779584
used_memory_human:761.31K
used_memory_rss:742656
used_memory_rss_human:725.25K
used_memory_peak:856656
used_memory_peak_human:836.58K
total_system_memory:0
total_system_memory_human:0B
used_memory_lua:37888
used_memory_lua_human:37.00K
maxmemory:0
maxmemory_human:0B
maxmemory_policy:noeviction
mem_fragmentation_ratio:0.95
mem_allocator:jemalloc-3.6.0
```

**网站数据统计**

- UV（Unique Visitor）

  独立访客，需通过用户IP【包括未登录访客】排重统计数据

  每次访问都要进行统计（访问量）

  HyperLogLog  性能好，且存储空间小

- DAU（Daily Active User）

  日活跃用户，需通过用户ID排重统计数据

  访问过一次，则认为其活跃

  Bitmap 性能好，且可以统计精确的结果

#### 任务执行和调度

服务器定时自动运行【定时启动定时运行，比如每隔一段时间计算帖子的score，每隔一段时间清理服务器的临时文件】

任务调度组件【多线程，需要使用线程池（创建线程开销较大）】

**JDK 线程池**

- ExecutorService

  普通的线程池，能够创建普通的线程

- ScheduledExecutorService

  定时线程池，创建的线程能够每隔一段时间执行任务

**SPRING 线程池**

- ThreadPoolTaskExecutor

  普通的线程池

- ThreadPoolTaskScheduler

  定时线程池

上述两种的定时线程池都是基于内存的，它们的配置参数是在内存中，不同服务器之间的内存不共享，也就无法沟通协作；Quartz则是基于数据库的，它将配置参数存在数据库中，不同服务器之间的Quartz的配置共同依赖于数据库，通过参数可协作

**分布式定时任务**

- Spring Quartz

  分布式环境下，普通的线程池可以正常使用，但是定时任务线程池就可能会存在问题，所以分布式定时任务可以使用Quartz

  建表 ：Quartz

  主要要注意的表：

  qrtz_job_details

  qrtz_simple_triggers

  qrtz_triggers

  qrtz_scheduler_state

  qrtz_locks

**热帖排行**

排行算法

log(精华分+评论数x10+点赞数x2+收藏数x2)+(发布时间-牛客纪元)【没有设置收藏功能，取消此项】

在每次有影响帖子score的操作出现时，把该帖子存到缓存（redis）中，每次定时计算缓存中的帖子score

执行算法的时机：使用定时任务，每隔一段时间计算一次score

新增的帖子给定一个score

#### 生成长图

**- wkhtmltopad**

下载，安装，配置环境变量，

- wkhtmltopad url file

- wkhtmltoimage [--quality 图片压缩百分比]  url file

  原图片占内存大，75%压缩后小且质量不受影响

**- java**

- Runtime.getRuntime().exec()

WKTest

在项目中进行配置（命令拼接，保存图片路径等等）

**将文件上传至云服务器**

- 客户端上传

  客户端将数据提交给云服务器，并等待其响应

  用户上传头像时，将表单数据提交给云服务器

- 服务器直传

  应用服务器将数据直接提交给云服务器，并等待其响应

  分享时，服务端将自动生成的图片，直接提交给云服务器

- 步骤：

  1.创建**对象存储**空间

  2.导入qiniu sdk依赖包

  3.配置密钥（AK,SK）

  4.配置创建的对象存储空间的名字和域名

  5.废弃之前换头像和获取头像图片方法

  6.在settingPage方法中设置上传文件名称，设置响应信息（StringMap），生成上传凭证（Auth）

  7.修改前端，编写异步方法

  8.废弃从本地获取分享图片的方法，修改share方法，修改返回分享图片的路径

  9.修改消费者消费分享事件过程：增加上传分享截图到云服务器上的代码

#### 优化网站的性能

优化网站的性能，加缓存是非常有效的手段

优化热门的帖子列表：使用缓存优化service

**本地缓存**

- 将数据缓存在应用服务器上，性能最好

- 常用的缓存工具：Ehcache，Guava，Caffeine等

- Caffeine使用实例

  可以单独使用（建议），也可以使用Spring整合

**分布式缓存**

- 将数据缓存在NoSQL数据库上，跨服务器

- 常用的缓存工具：MemCache，Redis等

**多级缓存**

- 一级缓存(本地缓存) >  二级缓存(分布式缓存)  > DB
- 避免缓存雪崩（缓存失效，大量请求直达DB），提高系统的可用性

**缓存淘汰策略**

JMeter 压力测试工具

利用工具模拟（多个）客户端访问服务器，查看性能

通过聚合报告查看结果：

不使用缓存：

80个线程：

![image-20210111181652772](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210111181652772.png)

100个线程：

![image-20210111181843167](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210111181843167.png)

使用缓存（caffeine）:

80个线程：

![image-20210111182155516](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210111182155516.png)

100个线程：

![image-20210111182330572](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210111182330572.png)

#### 项目发布和总结

**单元测试**

- Spring Boot Testing

  依赖：Spring-boot-starter-test

  包括了：Junit、Spring Test、AssertJ

- Test Case

  要求：保证测试方法的独立性

  步骤：初始化数据、执行测试代码、验证测试结果、清理测试数据

  常用注解：@BeforeClass，@AfterClass，@Before，@After

**项目监控**

- Spring boot Actuator

  - Endpoints：监控应用的入口，Spring boot内置了很多端点，也支持自定义端点
  - 监控方式：HTTP 或 JMX
  - 访问路径：例如 "/actuator/health"  health是端点的id
  - 注意事项：按需配置暴露的端点，并对所有端点进行**权限控制**

  导入依赖包即生效

  它有20+个端点，默认只有一个关闭服务器的端点没启用；默认只暴露了两个端点 health，info

  返回的是JSON数据

**项目部署**

![image-20210111192646726](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210111192646726.png)

浏览器通过nginx访问应用（反向代理），nginx将请求分发给服务器（Tomcat 非内嵌的）

通过Putty访问云服务器

尽量使用yum命令在服务器（CentOS）安装需要的应用（MySQL，Redis等等），yum不足的使用安装包链接下载

通过下载链接安装，putty云服务器命令：

```shell
wget -i -c 下载链接
```

本地向云服务器上传文件（压缩包），通过本地命令行：cd 到该目录下

```shell
pscp 压缩包名 root@ip地址:/保存在云服务器的位置(路径)
填写root的密码：
```

下载解压缩的工具：yum

```shell
# yum库里搜索unzip开头的资源
yum list unzip*
# 安装unzip  -y 表示在需要确认的时候都输入 y
yum install -y unzip.x86_64
# 先安装 jdk(open-jdk最新版) yum
# 解压maven
```

......

**项目总结**

![image-20210111204758863](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210111204758863.png)

**常见面试题**

- MySQL

  **存储引擎**（MyISAM,InnoDB）

  **事务**（特性ACID，隔离性【并发异常，隔离级别】，Spring事务管理）

  **锁**

  - 范围

    表级锁（锁整个表，MyISAM默认）：开销小，加锁快，发生锁冲突的概率高，并发度低，不会出现死锁

    行级锁（锁一行记录，InnoDB默认）：开销大，加锁慢，发生锁冲突的概率地，并发度高，会出现死锁

  - 类型（InnoDB）

    共享锁（S）：行级，读取一行

    排他锁（X）：行级，更新一行

    意向共享锁（IS）：表级，准备加共享锁

    意向排他锁（IX）：表级，准备加排他锁

    间隙锁（NK）：行级（发生在查询时），使用范围条件时，对范围内不存在的记录加锁。一是为了防止幻读，二是为了满足恢复和复制的需要。

  - 加锁

    - 增加行级锁之前，InnoDB会**自动**给表加意向锁

    - 执行DML语句时，Innodb会**自动**给数据加排他锁

    - 执行DQL语句时

      共享锁（S）

      ```SELECT ... FROM WHERE ... LOCK IN SHARE MODE;```

      排他锁（X）

      ```SELECT ... FROM WHERE ... FOR UPDATE;```

      间隙锁（NK）

      上述SQL采用范围条件时，InnoDB对不存在的记录自动加间隙锁

  - 死锁

    - 场景

      事务1：

      ```UPDATE T SET ... WHERE ID = 1;```  加锁（排他锁）

      ```UPDATE T SET ... WHERE ID = 2;```

      事务2：

      ```UPDATE T SET ... WHERE ID = 2;```  加锁（排他锁）

      ```UPDATE T SET ... WHERE ID = 1;```

    - 解决方案

      1.一般InnoDB会自动检测到，并使一个事务回滚，另一个事务继续执行

      2.设置超时等待参数 innodb_lock_wait_timeout

    - 避免死锁

      1.不同的业务并发访问多个表时，应约定以相同的顺序来访问这些表

      2.以批量的方式处理数据时，应事先对数据排序，保证线程按固定的顺序来处理数据

      3.在事务中，如果要更新记录，应直接申请足够级别的锁，即排他锁

  - 悲观锁，乐观锁

    - 悲观锁（数据库加的锁都是）

    - 乐观锁（自定义的，查询多，更新少时效率高）

      1.版本号机制（每个表加入VERSION字段）MVVC

      ```UPDATE ... SET ... ,VERSION=#{version+1} WHERE ... AND VERSION=${version}```

      2.CAS算法（Compare and swap）

      是一种无锁的算法，该算法涉及三个操作数（内存值V，旧值A，新值B），当V等于A时，采用原子方式用B的值更新V的值。

      该算法通常采用自旋操作，也叫自旋锁。

      它的缺点是：

      - ABA问题：某线程将A改为B，再改回A，则CAS会误认为A没被修改过
      - 自旋操作采用循环（线程不断等待直至解锁）的方式实现，若加锁时间长，则会给CPU带来巨大的开销
      - CAS只能保证一个共享变量的原子操作

  **索引**

  - B+Tree（InnoDB）

    - 数据分块存储，每一块称为一页（数据）

    - 所有的值都是按顺序存储的，并且每一个叶子到根的距离相同

    - 非叶节点存储数据的边界，叶子节点存储指向数据行的指针

    - 通过边界缩小数据的范围，从而避免全表扫描，加快了查找的速度

    ![image-20210112181015311](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210112181015311.png)

- Redis

  **数据类型**（内存的使用情况以及极限）

  ![image-20210112180954758](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210112180954758.png)

  注：bitmap本质上也就是字符串

  **过期策略**

  Redis会把设置了过期时间的key放入一个独立的字典里，在key过期时并不会立刻删除它。

  Redis会通过如下两种策略，来删除过期的key：

  - 惰性删除

    客户端访问某个key时，Redis会检查该key是否过期，若过期则删除（若存在某些Key一直不被访问，会消耗内存）

  - 定期扫描

    Redis默认每秒执行10次过期扫描（配置hz选项），扫描策略如下：

    1.从过期字典中随机选择20个key

    2.删除这20个key中已过期的key

    3.如果过期的key的比例超过25%，则重复步骤1

  **淘汰策略**

  当Redis占用内存超过最大限制（maxmemory）时，可采用如下策略（maxmemory-policy），让redis淘汰一些数据，以腾出空间继续提供读写服务：

  - noeviction  对可能导致增大内存的命令返回错误（大多数写命令，DELETE除外）
  - volatile-ttl  在设置了过期时间的key中，选择寿命（TTL）最短的key，将其淘汰
  - volatile-lru  在设置了过期时间的key中，选择最少使用的key（LRU），将其淘汰
  - volatile-random  在设置了过期时间的key中，随机选择一些key，将其淘汰
  - allkeys-lru  在所有的key中，选择最少使用的key（LRU），将其淘汰
  - allkeys-random  在所有的key中，随机选择一些key，将其淘汰

  **LRU算法**

  维护一个链表，用于顺序存储被访问过的key。

  在访问数据时，最新访问过的key将被移动到表头，即最近访问的key在表头，最少访问的key在表尾。

  **近似LRU算法**(Redis)

  给每个key维护一个时间戳，淘汰时随机采样5个key，从中淘汰掉最旧的key。如果还是超出内存限制，则继续随机采样。

  优点：比LRU算法节约内存，却可以取得非常近似的效果。

  **缓存穿透**

  场景：

  查询根本不存在的数据，使得请求直达存储层（数据库），导致其负载过大，甚至宕机。

  ![image-20210112184316259](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210112184316259.png)

  解决方案：

  1.缓存空对象：存储层未命中后，仍然将空值存入缓存层（redis）。再次访问该数据时，缓存层会直接返回空值。

  2.布隆过滤器（redis自带的）：将所有存在的key提前存入布隆过滤器，在访问缓存层之前，先通过过滤器拦截，若请求的是不存在的key，则直接返回空值。

  **缓存击穿**

  场景：

  一份热点数据，它的访问量特别大。在其缓存失效瞬间，大量请求直达存储层，导致服务崩溃。

  解决方案：

  1.加互斥锁：对数据的访问加互斥锁，当一个线程访问该数据时，其他线程只能等待。这个线程访问过后，缓存中的数据将被重建，届时其他线程就可以直接从缓存中取值。

  2.永不过期：不设置过期时间，所以不会出现上述问题，这是“物理”上的不过期。为每个value设置逻辑过期时间，当发现该值逻辑过期时，使用单独的线程重建缓存。

  **缓存雪崩**

  场景：

  由于某些原因，缓存层**不能提供**服务，导致所有请求直达存储层，造成存储层宕机。

  解决方案：

  1.避免同时过期。设置过期时间，附加一个随机数，避免大量的key同时过期

  2.构建高可用的redis缓存。部署多个redis实例，个别节点宕机，依然可以保持服务整体可用

  3.构建多级缓存。增加本地缓存，在存储层前面多加一级屏障，降低请求直达存储层的几率

  4.启用限流和降级措施。对存储层增加限流措施，当请求超出限制时，对其提供降级服务

  **分布式锁**

  场景：

  修改时，经常需要先将数据读取到内存，在内存中修改后再存回去。在分布式应用中，可能多个线程同时执行上述操作，而读取和修改非原子操作，所以会产生冲突。增加分布式锁，可以解决此类问题。

  基本原理：

  同步锁：在多个线程都能访问到的地方，做一个标记，标识该数据的访问权限

  分布式锁：在多个进程都能访问到的地方，做一个标记，标识该数据的访问权限

  实现方式：

  1.基于数据库实现分布式锁

  2.基于Redis实现分布式锁

  3.基于Zookeeper实现分布式锁

  - Redis实现分布式锁的原则：

    1.安全属性：独享。在任一时刻，只有一个客户端持有锁

    2.活性A：无死锁。即便持有锁的客户端崩溃或者网络被分裂，锁          仍然可以被获取

    3.活性B：容错。只要大部分Redis节点都活着，客户端就可以获取和释放锁

  - 单Redis实例实现分布式锁：

    1.获取锁使用命令：

    SET resource_name my_random_value NX PX 30000

    resource_name 指的是key

    NX：仅在key不存在时才执行成功。PX：设置锁的自动过期时间

    2.通过Lua脚本释放锁：

    ```shell
    if redis.call("get",KEYS[1]) == ARGV[1] then
    	return redis.call("del",KEYS[1])
    else return 0 end
    ```

    使用Lua脚本可以避免删除别的客户端获取成功的锁：

    A加锁 => A阻塞 => 因超时释放锁 => B加锁 => A恢复 => 释放锁

  - 多Redis实例实现分布式锁

    Redlock算法，该算法有现成的实现，其 Java 版本的库为Redisson

    1.获取当前Unix时间，以毫秒为单位

    2.依次尝试从N个实例，使用相同的key和随机值获取锁，并设置响应超时时间，如果服务器没有在规定时间内响应，客户端应该尽快尝试另一个Redis实例

    3.客户端使用当前时间减去开始获取锁的时间，得到获取锁使用的时间。当且仅当大多数的Redis节点都取到锁，并且使用的时间小于锁失效时间时，锁才算取到成功

    4.如果取到了锁，key的真正有效时间等于有效时间减去获取锁使用的时间

    5.如果获取锁失败，客户端应该在所有的Redis实例上进行解锁

- Spring

  **IOC**
  
  ![image-20210112202232684](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210112202232684.png)
  
  **AOP**
  
  ![image-20210112202428266](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210112202428266.png)
  
  **Spring MVC**
  
  ![image-20210112202501164](C:\Users\86136\AppData\Roaming\Typora\typora-user-images\image-20210112202501164.png)
  
  









