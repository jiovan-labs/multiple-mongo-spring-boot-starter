[English](README.md) | [簡體中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# 多 MongoDB Spring Boot Starter

讓 Spring Boot 應用快速整合多個 `MongoDB`。

## 快速開始

- 匯入相依性

```xml
    <dependency>
        <groupId>com.jiovan.spring.starter</groupId>
        <artifactId>multiple-mongo-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> 預設情況下，此 starter 會自動生效，你可以透過 `jiovan.multiple-mongo.enabled = false` 關閉它。

- 使用字首 `jiovan.multiple-mongo` 設定 Spring Boot 的 `application.yml`

```yml
jiovan:
    multiple-mongo:
        primary:
            host: '192.168.0.1'
            database: 'foo1'
        secondary:
            host: '192.168.0.2'
            database: 'foo2'
        tertiary:
            host: '192.168.0.3'
            database: 'foo3'
```

> 此 starter 最多支援 3 個 `MongoClientFactory`。（三振出局）

- **可選功能**：如果要使用 MongoDB 倉儲，請將倉儲類放在以下包中（以 `primary` 為例）

    - 倉儲類：`**.repository.primary.mongo`

- 透過 `@Autowired`/`@Resource` 註解並配合 `@Qualifier` 註解注入以下 Bean（以 `primary` 為例）

| Bean 型別 | 限定符 |
|-------------------------|----------------------------------------------------|
| MongoClientFactory | PrimaryMongoAutoConfiguration.CLIENT_FACTORY |
| MongoClient | PrimaryMongoAutoConfiguration.MONGO_CLIENT |
| MongoTransactionManager | PrimaryMongoAutoConfiguration.TRANSACTION_MANAGER |
| MongoTemplate | PrimaryMongoAutoConfiguration.MONGO_TEMPLATE |
| GridFsTemplate | PrimaryMongoAutoConfiguration.GRID_FS_TEMPLATE |
| ReactiveMongoTemplate | PrimaryMongoReactiveConfiguration.MONGO_TEMPLATE |
| ReactiveGridFsTemplate | PrimaryMongoReactiveConfiguration.GRID_FS_TEMPLATE |

## 文件

- GitHub：https://github.com/jiovan-labs/multiple-mongo-spring-boot-starter
- MongoDB：https://mongodb.com

## 環境要求

- jdk 17+

## 許可證

本專案採用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 許可證。

請參閱 `NOTICE.txt` 檔案瞭解所需的宣告與歸屬資訊。

## 捐贈

喜歡這個工具包嗎？歡迎[向我們捐贈](https://jiovan.com/donation)以支援開發。

## 版權

北京玖漫科技有限公司

## 網站

- 玖漫：https://jiovan.com
