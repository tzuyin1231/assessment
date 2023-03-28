本專案assessment是一個論壇的後端server
使用者先獲取JWT token後，帶著JWT請求GraphQL，即可使用增刪改查的功能

token獲取方式的API為HTTP請求，其餘資料則透過GraphQL作為API溝通方式
使用spring boot作為後端框架，並使用spring data JPA作為和資料庫的串接
使用postgresQL作為資料庫

啟用server方式:
 * $ cd 到本目錄
 * $ docker-compose up
 * server即啟用完成

只啟用資料庫方式:
 * 確認application.yml檔內資料庫連線設定為 url: jdbc:postgresql://localhost:32770/assessment
 * $ cd 到本目錄
 * $ docker-compose -f docker-compose-DB-only.yml up
 * 資料庫即啟用完成，之後可以用自己本機的IDE起後端
