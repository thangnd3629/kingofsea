# Asset Service Module

## Usage

1. Import module into your project
2. Add do space config to your application context

```
do:
  space:
    key: <your key>
    secret: <your secret>
    endpoint: <your endpoint>
    region: <your region>
    bucket: <your bucket>
```

3. Add package ```"com.supergroup.asset.*"``` to ```baseScanPackages``` in ```@ComponentScan``` annotation.

## Features

#### Save file

```java
AssetService.saveFile(MultipartFile file, String key)
```

file: is multipart file, which you want to save

key: is absolute path you want to save it in Digital Ocean Space (ex: /asset/test/test.jpg)

Function return url saved file (ex: https://radiantgalaxy.sgp1.digitaloceanspaces.com/asset/test/test.jpg)

#### Delete file

```java
AssetService.deleteFile(String key)
```

key: is absolute path you want to delete (ex: /asset/test/test.jpg)
