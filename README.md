# moments_v2_backend (Work In Progress)
backend for a sharing app using SpringBoot, Redis, MySQL, and AWS S3.

This is the second version of my project [ShareOurDays](https://github.com/AllenSun-HM/ShareOurDays)'s backend. I added more features like getting most-popular users or posts.
I built this project while learning and using SpringBoot, Redis, and MySQL.

## How to Maintain the Consistency between MySQL and Redis?
 ### Query Data Process 
 ![Query Data Process](https://general-pics-allen.s3.ap-northeast-2.amazonaws.com/github_moments_v2_backend/Query+Data+Process+(1).png)
 ### Delete Data Process 
 ![Delete Data Process](https://general-pics-allen.s3.ap-northeast-2.amazonaws.com/github_moments_v2_backend/Delete+Data+Process+(1).png)
 ### Insert/Update Data Process 
 ![Insert/Update Data Process](https://general-pics-allen.s3.ap-northeast-2.amazonaws.com/github_moments_v2_backend/Insert_Update+Data+Process+(3).png)

## TODOS
  1. add ElasticSearch for user & post searching feature;
  2. configure MySQL clusters to separate db-read and db-write to MySQL master node and slave node;
