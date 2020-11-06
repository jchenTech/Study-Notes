## 1 数据库相关介绍

### 1.1 数据库的好处

1. 持久化数据到本地
2. 可以实现结构化查询，方便管理

### 1.2 数据库相关概念
1. DB：数据库，保存一组有组织的数据的容器
2. DBMS：数据库管理系统，又称为数据库软件（产品），用于管理DB中的数据
3. SQL:结构化查询语言，用于和DBMS通信的语言


### 1.3 数据库存储数据的特点

1. 将数据放到表中，表再放到库中
2. 一个数据库中可以有多个表，每个表都有一个的名字，用来标识自己。表名具有唯一性。
3. 表具有一些特性，这些特性定义了数据在表中如何存储，类似Java中 “类”的设计。
4. 表由列组成，我们也称为字段。所有表都是由一个或多个列组成的，每一列类似Java 中的”属性”
5. 表中的数据是按行存储的，每一行类似于Java中的“对象”。


## 2 MySQL产品的介绍和安装

### 2.1 MySQL服务的启动和停止

* 方式一：计算机——右击管理——服务
* 方式二：通过管理员身份运行
  net start 服务名（启动服务）
  net stop 服务名（停止服务）

### 2.2 MySQL服务的登录和退出

* 方式一：通过MySQL自带的客户端
  只限于root用户

* 方式二：通过windows自带的客户端
  登录：
  `mysql 【-h主机名 -P端口号 】-u用户名 -p密码`

  退出：
  `exit`或`ctrl+C`


### 2.3 MySQL的常见命令 

1. 查看当前所有的数据库
   `show databases;`

2. 打开指定的库
   `use 库名`

3. 查看当前库的所有表
   `show tables;`

4. 查看其它库的所有表
   `show tables from 库名;`

5. 创建表

   ```mysql
   create table 表名(
   列名 列类型,
   列名 列类型，
   ...
   );
   ```

6. 查看表结构
   `desc 表名;`

7. 查看服务器的版本

   * 方式一：登录到MySQL服务端
     `select version();`
   * 方式二：没有登录到MySQL服务端
     `mysql --version` 或 `mysql --V`

### 2.4 MySQL的语法规范

1. 不区分大小写,但建议关键字大写，表名、列名小写
2. 每条命令最好用分号结尾
3. 每条命令根据需要，可以进行缩进 或换行
4. 注释
   单行注释：`#`注释文字
   单行注释：`--` 注释文字
   多行注释：`/* 注释文字  */`

### 2.5 SQL的语言分类

1. `DQL(Data Query Language)`：数据查询语言`select` 
2. `DML(Data Manipulate Language)`:数据操作语言`insert 、update、delete`
3. `DDL(Data Define Languge)`：数据定义语言`create、drop、alter`
4. `TCL(Transaction Control Language)`：事务控制语言`commit、rollback`

### 2.6 SQL的常见命令

* `show databases；` 查看所有的数据库
* `use 库名；` 打开指定 的库
* `show tables ;` 显示库中的所有表
* `show tables from` 库名;显示指定库中的所有表

* `create table 表名( 字段名 字段类型,	字段名 字段类型, ... );` 创建表

* `desc 表名;` 查看指定表的结构
* `select * from 表名;`显示表中的所有数据

## 3 DQL语言

### 3.1 基础查询

语法：`select 查询列表 from 表名;`

特点：

1. 查询列表可以是字段、常量、表达式、函数，也可以是多个
2. 查询结果是一个虚拟表

示例：

1. 查询表中的单个字段

   ```mysql
   SELECT last_name FROM employees;
   ```

2. 查询表中的多个字段

   ```mysql
   SELECT last_name,salary,email FROM employees;
   ```

3. 查询表中的所有字段

    ```mysql
   SELECT * FROM employees;
   ```

4. 查询常量值

   ```mysql
   SELECT 100;
   SELECT 'john';
   ```

5. 查询表达式

   ```mysql
    SELECT 100%98;
   ```

6. 查询函数

   ```mysql
   SELECT VERSION();
   ```

7. 起别名

   ```mysql
   SELECT last_name AS 姓,first_name AS 名 FROM employees;
   SELECT salary AS "out put" FROM employees;
   ```

8. 去重

   #案例：查询员工表中涉及到的所有的部门编号

   ```mysql
   SELECT DISTINCT department_id FROM employees;
   ```

9. +号的作用

   * Java中的+号：
     ①运算符，两个操作数都为数值型
     ②连接符，只要有一个操作数为字符串

   * MySQL中的+号：
     仅仅只有一个功能：运算符

     `select 100+90;` 两个操作数都为数值型，则做加法运算
     `select '123'+90;`只要其中一方为字符型，试图将字符型数值转换成数值型如果转换成功，则继续做加法运算
     `select 'john'+90;`	如果转换失败，则将字符型数值转换成0

     `select null+10;` 只要其中一方为null，则结果肯定为null

10. concat函数
    案例：查询员工名和姓连接成一个字段，并显示为姓名

    ```mysql
    SELECT 
    	CONCAT(last_name,first_name) AS 姓名
    FROM
    	employees;
    ```

11. ifnull函数
    功能：判断某字段或表达式是否为null，如果为null 返回指定的值，否则返回原本的值

    ```sql
    SELECT ifnull(commission_pct,0) FROM employees;
    ```

12. isnull函数
    功能：判断某字段或表达式是否为null，如果是，则返回1，否则返回0

### 3.2 条件查询

语法：

```mysql
select 
	查询列表
from
	表名
where
	筛选条件;
```

分类：

1. 按条件表达式筛选
简单条件运算符：`>` , `<`,   `=`,  `<>`,   `>=`,  `<=`
2. 按逻辑表达式筛选
   作用：用于连接条件表达式
   `and` , `or` , `not`
3. 模糊查询
    `like`,  `between and`,  `in`,  `is null/is not null`

示例：

### 3.3 排序查询

语法：

```mysql
select 查询列表
from 表名
【where  筛选条件】
order by 排序的字段或表达式;
```

特点：

1. `asc`代表的是升序，可以省略
   `desc`代表的是降序
2. order by子句可以支持 单个字段、别名、表达式、函数、多个字段
3. order by子句在查询语句的最后面，除了limit子句

### 3.4 常见函数

一. 单行函数

1. 字符函数
   * `concat`拼接
   * `substr`截取子串
   * `upper`转换成大写
   * `lower`转换成小写
   * `trim`去前后指定的空格和字符
   * `ltrim`去左边空格
   * `rtrim`去右边空格
   * `replace`替换
   * `lpad`左填充
   * `rpad`右填充
   * `instr`返回子串第一次出现的索引
   * `length` 获取字节个数
2. 数学函数
   * `round` 四舍五入

   * `rand` 随机数

   * `floor`向下取整

   * `ceil`向上取整

   * `mod`取余

   * `truncate`截断
3. 日期函数
     	* `now`当前系统日期+时间
     * `curdate`当前系统日期
     * `curtime`当前系统时间
     * `str_to_date` 将字符转换成日期
     * `date_format`将日期转换成字符
4. 流程控制函数
     	* `if` 处理双分支
     * `case`语句 处理多分支
     * 情况1：处理等值判断
     * 情况2：处理条件判断


5. 其他函数
     	* `version`版本
     * `database`当前库
     * `user`当前连接用户

```mysql
#一、字符函数

#1.length 获取参数值的字节个数
SELECT LENGTH('john');
SELECT LENGTH('张三丰hahaha');

SHOW VARIABLES LIKE '%char%'

#2.concat 拼接字符串

SELECT CONCAT(last_name,'_',first_name) 姓名 FROM employees;

#3.upper、lower
SELECT UPPER('john');
SELECT LOWER('joHn');
#示例：将姓变大写，名变小写，然后拼接
SELECT CONCAT(UPPER(last_name),LOWER(first_name))  姓名 FROM employees;

#4.substr、substring
注意：索引从1开始
#截取从指定索引处后面所有字符
SELECT SUBSTR('李莫愁爱上了陆展元',7)  out_put;

#截取从指定索引处指定字符长度的字符
SELECT SUBSTR('李莫愁爱上了陆展元',1,3) out_put;


#案例：姓名中首字符大写，其他字符小写然后用_拼接，显示出来

SELECT CONCAT(UPPER(SUBSTR(last_name,1,1)),'_',LOWER(SUBSTR(last_name,2)))  out_put
FROM employees;

#5.instr 返回子串第一次出现的索引，如果找不到返回0

SELECT INSTR('杨不殷六侠悔爱上了殷六侠','殷八侠') AS out_put;

#6.trim

SELECT LENGTH(TRIM('    张翠山    ')) AS out_put;

SELECT TRIM('aa' FROM 'aaaaaaaaa张aaaaaaaaaaaa翠山aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')  AS out_put;

#7.lpad 用指定的字符实现左填充指定长度

SELECT LPAD('殷素素',2,'*') AS out_put;

#8.rpad 用指定的字符实现右填充指定长度

SELECT RPAD('殷素素',12,'ab') AS out_put;


#9.replace 替换

SELECT REPLACE('周芷若周芷若周芷若周芷若张无忌爱上了周芷若','周芷若','赵敏') AS out_put;


#二、数学函数

#round 四舍五入
SELECT ROUND(-1.55);
SELECT ROUND(1.567,2);


#ceil 向上取整,返回>=该参数的最小整数

SELECT CEIL(-1.02);

#floor 向下取整，返回<=该参数的最大整数
SELECT FLOOR(-9.99);

#truncate 截断

SELECT TRUNCATE(1.69999,1);

#mod取余
/*
mod(a,b) ：  a-a/b*b

mod(-10,-3):-10- (-10)/(-3)*（-3）=-1
*/
SELECT MOD(10,-3);
SELECT 10%3;


#三、日期函数

#now 返回当前系统日期+时间
SELECT NOW();

#curdate 返回当前系统日期，不包含时间
SELECT CURDATE();

#curtime 返回当前时间，不包含日期
SELECT CURTIME();

#可以获取指定的部分，年、月、日、小时、分钟、秒
SELECT YEAR(NOW()) 年;
SELECT YEAR('1998-1-1') 年;

SELECT  YEAR(hiredate) 年 FROM employees;

SELECT MONTH(NOW()) 月;
SELECT MONTHNAME(NOW()) 月;

#str_to_date 将字符通过指定的格式转换成日期

SELECT STR_TO_DATE('1998-3-2','%Y-%c-%d') AS out_put;

#查询入职日期为1992--4-3的员工信息
SELECT * FROM employees WHERE hiredate = '1992-4-3';

SELECT * FROM employees WHERE hiredate = STR_TO_DATE('4-3 1992','%c-%d %Y');


#date_format 将日期转换成字符

SELECT DATE_FORMAT(NOW(),'%y年%m月%d日') AS out_put;

#查询有奖金的员工名和入职日期(xx月/xx日 xx年)
SELECT last_name,DATE_FORMAT(hiredate,'%m月/%d日 %y年') 入职日期
FROM employees
WHERE commission_pct IS NOT NULL;


#四、其他函数

SELECT VERSION();
SELECT DATABASE();
SELECT USER();


#五、流程控制函数
#1.if函数： if else 的效果

SELECT IF(10<5,'大','小');

SELECT last_name,commission_pct,IF(commission_pct IS NULL,'没奖金，呵呵','有奖金，嘻嘻') 备注
FROM employees;


#2.case函数的使用一： switch case 的效果

/*
java中
switch(变量或表达式){
	case 常量1：语句1;break;
	...
	default:语句n;break;


}

mysql中

case 要判断的字段或表达式
when 常量1 then 要显示的值1或语句1;
when 常量2 then 要显示的值2或语句2;
...
else 要显示的值n或语句n;
end
*/

/*案例：查询员工的工资，要求

部门号=30，显示的工资为1.1倍
部门号=40，显示的工资为1.2倍
部门号=50，显示的工资为1.3倍
其他部门，显示的工资为原工资

*/

SELECT salary 原始工资,department_id,
CASE department_id
WHEN 30 THEN salary*1.1
WHEN 40 THEN salary*1.2
WHEN 50 THEN salary*1.3
ELSE salary
END AS 新工资
FROM employees;

#3.case 函数的使用二：类似于 多重if
/*
java中：
if(条件1){
	语句1；
}else if(条件2){
	语句2；
}
...
else{
	语句n;
}

mysql中：

case 
when 条件1 then 要显示的值1或语句1
when 条件2 then 要显示的值2或语句2
。。。
else 要显示的值n或语句n
end
*/

#案例：查询员工的工资的情况
如果工资>20000,显示A级别
如果工资>15000,显示B级别
如果工资>10000，显示C级别
否则，显示D级别


SELECT salary,
CASE 
WHEN salary>20000 THEN 'A'
WHEN salary>15000 THEN 'B'
WHEN salary>10000 THEN 'C'
ELSE 'D'
END AS 工资级别
FROM employees;
```

 二. 分组函数（聚合函数）

* `sum` 求和
* `max` 最大值
* `min` 最小值
* `avg` 平均值
* `count` 计数

特点：

1. 以上五个分组函数都忽略null值，除了`count(*)`
2. `sum`和`avg`一般用于处理数值型
   `max`、`min`、`count`可以处理任何数据类型
3. 都可以搭配`distinct`使用，用于统计去重后的结果
4. `count`的参数可以支持：
   	字段、*、常量值，一般放1

   建议使用 count(*)

### 3.5 分组查询

语法：

```mysql
select 查询的字段，分组函数
from 表
group by 分组的字段
```

特点：

1. 可以按单个字段分组

2. 和分组函数一同查询的字段最好是分组后的字段

3. 分组筛选

   |              | 针对的表       | 位置           | 关键字 |
   | ------------ | -------------- | -------------- | ------ |
   | 分组前筛选： | 原始表         | group by的前面 | where  |
   | 分组后筛选： | 分组后的结果集 | group by的后面 | having |

4. 可以按多个字段分组，字段之间用逗号隔开
5. 可以支持排序
6. `having`后可以支持别名

```mysql
#1.简单的分组

#案例1：查询每个工种的员工平均工资
SELECT AVG(salary),job_id
FROM employees
GROUP BY job_id;

#案例2：查询每个位置的部门个数

SELECT COUNT(*),location_id
FROM departments
GROUP BY location_id;

#2、可以实现分组前的筛选
#案例1：查询邮箱中包含a字符的 每个部门的最高工资

SELECT MAX(salary),department_id
FROM employees
WHERE email LIKE '%a%'
GROUP BY department_id;

#案例2：查询有奖金的每个领导手下员工的平均工资

SELECT AVG(salary),manager_id
FROM employees
WHERE commission_pct IS NOT NULL
GROUP BY manager_id;

#3、分组后筛选
#案例2：每个工种有奖金的员工的最高工资>12000的工种编号和最高工资

SELECT job_id,MAX(salary)
FROM employees
WHERE commission_pct IS NOT NULL
GROUP BY job_id
HAVING MAX(salary)>12000;


#案例3：领导编号>102的每个领导手下的最低工资大于5000的领导编号和最低工资

SELECT manager_id,MIN(salary)
FROM employees
WHERE manager_id>102
GROUP BY manager_id
HAVING MIN(salary)>5000;


#4.添加排序
#案例：每个工种有奖金的员工的最高工资>6000的工种编号和最高工资,按最高工资升序

SELECT job_id,MAX(salary) m
FROM employees
WHERE commission_pct IS NOT NULL
GROUP BY job_id
HAVING m>6000
ORDER BY m ;

#5.按多个字段分组
#案例：查询每个工种每个部门的最低工资,并按最低工资降序

SELECT MIN(salary),job_id,department_id
FROM employees
GROUP BY department_id,job_id
ORDER BY MIN(salary) DESC;
```

综上所述，各语句的执行顺序为：

1. from
2. where
3. group by
4. having
5. select
6. order by

### 3.6 多表连接查询

一、传统模式下的连接 ：等值连接——非等值连接

1. 等值连接的结果 = 多个表的交集
2. n表连接，至少需要n-1个连接条件
3. 多个表不分主次，没有顺序要求
4. 一般为表起别名，提高阅读性和性能

二、sql99语法：通过join关 键字实现连接

含义：1999年推出的sql语法
支持：

* 等值连接、非等值连接 （内连接）
* 外连接
* 交叉连接

语法：

```mysql
select 字段，...
from 表1
[inner|left outer|right outer|cross]join 表2 on  连接条件
[inner|left outer|right outer|cross]join 表3 on  连接条件
[where 筛选条件]
[group by 分组字段]
[having 分组后的筛选条件]
[order by 排序的字段或表达式]
```

好处：语句上，连接条件和筛选条件实现了分离，简洁明了！


三、自连接

案例：查询员工名和直接上级的名称

sql99

```mysql
SELECT e.last_name,m.last_name
FROM employees e
JOIN employees m ON e.`manager_id`=m.`employee_id`;
```

sql92


```mysql
SELECT e.last_name,m.last_name
FROM employees e,employees m 
WHERE e.`manager_id`=m.`employee_id`;
```

四、外连接

应用场景：一般用于查询主表中有但从表中没有的记录

特点：

1. 外连接分主从表，两表的顺序不能任意调换
2. 左连接的话，左边为主表
   右连接的话，右边为主表

语法：

```mysql
select 查询列表
from 表1
left|right [outer] join 表2 on 连接条件
where 筛选条件
group by 分组字段
having 分组后的筛选条件
order by 排序的字段或表达式
```

```mysql
#一、查询编号>3的女神的男朋友信息，如果有则列出详细，如果没有，用null填充
SELECT b.id,b.name,bo.*
FROM beauty b
LEFT OUTER JOIN boys bo
ON b.`boyfriend_id` = bo.`id`
WHERE b.`id`>3;

#二、查询哪个城市没有部门
SELECT city
FROM departments d
RIGHT OUTER JOIN locations l 
ON d.`location_id`=l.`location_id`
WHERE  d.`department_id` IS NULL;

#三、查询部门名为SAL或IT的员工信息
SELECT e.*,d.department_name,d.`department_id`
FROM departments  d
LEFT JOIN employees e
ON d.`department_id` = e.`department_id`
WHERE d.`department_name` IN('SAL','IT');

SELECT * FROM departments
WHERE `department_name` IN('SAL','IT');
```

 ### 3.7 子查询

含义：

一条查询语句中又嵌套了另一条完整的select语句，其中被嵌套的select语句，称为子查询或内查询；在外面的查询语句，称为主查询或外查询



分类：

1. 按子查询出现的位置：
   * select后面：
     仅仅支持标量子查询
   * from后面：
     支持表子查询
   * where或having后面：★
     标量子查询（单行）
     列子查询  （多行）
     行子查询
   * exists后面（相关子查询）
     表子查询

2. 按结果集的行列数不同：
   * 标量子查询（结果集只有一行一列）
   * 列子查询（结果集只有一列多行）
   * 行子查询（结果集有一行多列）
   * 表子查询（结果集一般为多行多列）

```mysql
#1.标量子查询

#案例1：谁的工资比 Abel 高?

#①查询Abel的工资
SELECT salary
FROM employees
WHERE last_name = 'Abel'

#②查询员工的信息，满足 salary>①结果
SELECT *
FROM employees
WHERE salary>(
	SELECT salary
	FROM employees
	WHERE last_name = 'Abel'

);

#2.列子查询（多行子查询）★
#案例1：返回location_id是1400或1700的部门中的所有员工姓名

#①查询location_id是1400或1700的部门编号
SELECT DISTINCT department_id
FROM departments
WHERE location_id IN(1400,1700)

#②查询员工姓名，要求部门号是①列表中的某一个

SELECT last_name
FROM employees
WHERE department_id  <>ALL(
	SELECT DISTINCT department_id
	FROM departments
	WHERE location_id IN(1400,1700)
);

#3、行子查询（结果集一行多列或多行多列）

#案例：查询员工编号最小并且工资最高的员工信息
SELECT * 
FROM employees
WHERE (employee_id,salary)=(
	SELECT MIN(employee_id),MAX(salary)
	FROM employees
);

#①查询最小的员工编号
SELECT MIN(employee_id)
FROM employees

#②查询最高工资
SELECT MAX(salary)
FROM employees

#③查询员工信息
SELECT *
FROM employees
WHERE employee_id=(
	SELECT MIN(employee_id)
	FROM employees

)AND salary=(
	SELECT MAX(salary)
	FROM employees

);

```

### 3.8 分页查询

应用场景：

实际的web项目中需要根据用户的需求提交对应的分页查询的sql语句



语法：

```mysql
select 字段|表达式,...
from 表
where 条件
group by 分组字段
having 条件
order by 排序的字段
limit 起始的条目索引，条目数;
```

特点：

1. 起始条目索引从0开始

2. limit子句放在查询语句的最后

3. 公式：select * from  表 limit （page-1）*sizePerPage,sizePerPage
   假如:
   每页显示条目数sizePerPage
   要显示的页数 page

```mysql
#案例1：查询前五条员工信息
SELECT * FROM  employees LIMIT 0,5;
SELECT * FROM  employees LIMIT 5;

#案例2：查询第11条——第25条
SELECT * FROM  employees LIMIT 10,15;

#案例3：有奖金的员工信息，并且工资较高的前10名显示出来
SELECT 
    * 
FROM
    employees 
WHERE commission_pct IS NOT NULL 
ORDER BY salary DESC 
LIMIT 10 ;
```

### 3.9 联合查询

union 联合 合并：

将多条查询语句的结果合并成一个结果

```mysql
select 字段|常量|表达式|函数 【from 表】 【where 条件】 union 【all】
select 字段|常量|表达式|函数 【from 表】 【where 条件】 union 【all】
select 字段|常量|表达式|函数 【from 表】 【where 条件】 union  【all】
.....
select 字段|常量|表达式|函数 【from 表】 【where 条件】
```

应用场景：
要查询的结果来自于多个表，且多个表没有直接的连接关系，但查询的信息一致时语法：



特点：

1. 多条查询语句的查询的列数必须是一致的
2. 多条查询语句的查询的列的类型几乎相同
3. union代表去重，union all代表不去重



```mysql
#引入的案例：查询部门编号>90或邮箱包含a的员工信息
SELECT * FROM employees WHERE email LIKE '%a%' OR department_id>90;;

SELECT * FROM employees  WHERE email LIKE '%a%'
UNION
SELECT * FROM employees  WHERE department_id>90;


#案例：查询中国用户中男性的信息以及外国用户中年男性的用户信息
SELECT id,cname FROM t_ca WHERE csex='男'
UNION ALL
SELECT t_id,tname FROM t_ua WHERE tGender='male';

```

## 4 DDL语言

### 4.1 库和表的管理

库的管理：

1. 创建库

```mysql
create database if exists 库名
```

2. 删除库

```mysql
drop database if exists 库名
```

表的管理：

1. 创建表

```mysql
#语法
create table 表名(
	列名 列的类型【(长度) 约束】,
	列名 列的类型【(长度) 约束】,
	列名 列的类型【(长度) 约束】,
	...
	列名 列的类型【(长度) 约束】
)
#案例：创建表Book
CREATE TABLE book(
	id INT,#编号
	bName VARCHAR(20),#图书名
	price DOUBLE,#价格
	authorId  INT,#作者编号
	publishDate DATETIME#出版日期
);
```

2. 修改表 alter

```mysql
#①修改列名
ALTER TABLE book CHANGE COLUMN publishdate pubDate DATETIME;

#②修改列的类型或约束
ALTER TABLE book MODIFY COLUMN pubdate TIMESTAMP;

#③添加新列
ALTER TABLE author ADD COLUMN annual DOUBLE; 

#④删除列
ALTER TABLE book_author DROP COLUMN  annual;

#⑤修改表名
ALTER TABLE author RENAME TO book_author;
```

3. 删除表

```mysql
DROP TABLE IF EXISTS book_author;
```

### 4.2 数据类型

1. 数值型
  * 整型
    `tinyint`、`smallint`、`mediumint`、`int/integer`、`bigint`
    1                            2                            3                           4                         8

  特点：

  * 都可以设置无符号和有符号，默认有符号，通过unsigned设置无符号
  * 如果超出了范围，会报out or range异常，插入临界值
  * 长度可以不指定，默认会有一个长度
    长度代表显示的最大宽度，如果不够则左边用0填充，但需要搭配`zerofill`，并且默认变为无符号整型

  * 小数

    定点数：`decimal(M,D)`
    浮点数:  `float(M,D)` /4 ；`double(M,D)` /8

    特点：

    * M：整数部位+小数部位
      D：小数部位
      如果超过范围，则插入临界值

    * M和D都可以省略
      如果是decimal，则M默认为10，D默认为0
      如果是float和double，则会根据插入的数值的精度来决定精度

    * 定点型的精确度较高，如果要求插入数值的精度较高如货币运算等则考虑使用

2. 字符型

   `char`、`varchar`、`binary`、`varbinary`、`enum`、`set`、`text`、`blob`

   * `char`：固定长度的字符，写法为char(M)，最大长度不能超过M，其中M可以省略，默认为1
   * `varchar`：可变长度的字符，写法为varchar(M)，最大长度不能超过M，其中M不可以省略

3. 日期型
   `year`年、`date`日期、`time`时间、`datetime` 日期+时间、`timestamp` 日期+时间、比较容易受时区、语法模式、版本的影响，更能反映当前时区的真实时间

### 4.3 常见的约束

含义：一种限制，用于限制表中的数据，为了保证表中的数据的准确和可靠性

分类：六大约束

* `NOT NULL`：非空，用于保证该字段的值不能为空
  比如姓名、学号等
* `DEFAULT`:默认，用于保证该字段有默认值
  比如性别
* `PRIMARY KEY`:主键，用于保证该字段的值具有唯一性，并且非空
  比如学号、员工编号等
* `UNIQUE`:唯一，用于保证该字段的值具有唯一性，可以为空
  比如座位号
* `CHECK`:检查约束【mysql中不支持】
  比如年龄、性别
* `FOREIGN KEY`:外键，用于限制两个表的关系，用于保证该字段的值必须来自于主表的关联列的值
  在从表添加外键约束，用于引用主表中某列的值
  比如学生表的专业编号，员工表的部门编号，员工表的工种编号

* 创建表时
* 修改表时
  	

约束的添加分类：

* 列级约束：
  六大约束语法上都支持，但外键约束没有效果

* 表级约束：
  除了非空、默认，其他的都支持



主键和唯一的大对比：

|      | 保证唯一性 | 是否允许为空 | 一个表中可以有多少个 | 是否允许组合 |
| ---- | ---------- | ------------ | -------------------- | ------------ |
| 主键 | 是         | 否           | 至多有1个            | 是，但不推荐 |
| 唯一 | 是         | 是           | 可以有多个           | 是，但不推荐 |



外键：

* 要求在从表设置外键关系
* 从表的外键列的类型和主表的关联列的类型要求一致或兼容，名称无要求
* 主表的关联列必须是一个key（一般是主键或唯一）
* 插入数据时，先插入主表，再插入从表
  删除数据时，先删除从表，再删除主表

## 5 DML语言

### 5.1 插入

语法：

```mysql
insert into 表名(列名,...) values(值1,...);
```

特点：

1. 要求值的类型和字段的类型要一致或兼容
2. 字段的个数和顺序不一定与原始表中的字段个数和顺序一致
   但必须保证值和字段一一对应
3. 假如表中有可以为null的字段，注意可以通过以下两种方式插入null值
   * 字段和值都省略
   * 字段写上，值使用null

4. 字段和值的个数必须一致
5. 字段名可以省略，默认所有列

```mysql
INSERT INTO beauty
VALUES(23,'唐艺昕1','女','1990-4-23','1898888888',NULL,2)
,(24,'唐艺昕2','女','1990-4-23','1898888888',NULL,2)
,(25,'唐艺昕3','女','1990-4-23','1898888888',NULL,2);
```

### 5.2 修改

二、修改语句

语法：

1. 修改单表的记录

```mysql
update 表名
set 列=新值,列=新值,...
where 筛选条件;
```

2. 修改多表的记录

```mysql
update 表1 别名 
left|right|inner join 表2 别名 
on 连接条件  
set 字段=值,字段=值 
【where 筛选条件】;
```

  具体案例：

```mysql
#1.修改单表的记录
#案例1：修改beauty表中姓唐的女神的电话为13899888899

UPDATE beauty SET phone = '13899888899'
WHERE NAME LIKE '唐%';

#案例2：修改boys表中id好为2的名称为张飞，魅力值 10
UPDATE boys SET boyname='张飞',usercp=10
WHERE id=2;

#2.修改多表的记录
#案例 1：修改张无忌的女朋友的手机号为114
UPDATE boys bo
INNER JOIN beauty b ON bo.`id`=b.`boyfriend_id`
SET b.`phone`='119',bo.`userCP`=1000
WHERE bo.`boyName`='张无忌';
```

### 5.3 删除

1. 方式一：

   * 使用delete

     删除单表的记录
     语法：

     ```mysql
     delete from 表名 【where 筛选条件】【limit 条目数】
     ```

   * 级联删除[补充]
     语法：

     ```mysql
     delete 别名1,别名2 from 表1 别名 
     inner|left|right join 表2 别名 
     on 连接条件
     【where 筛选条件】
     ```

2. 方式二：使用truncate
   语法：`truncate table 表名`



两种方式的区别【面试题】

1. truncate删除后，如果再插入，标识列从1开始
     delete删除后，如果再插入，标识列从断点开始
2. delete可以添加筛选条件
    truncate不可以添加筛选条件
3. truncate效率较高
4. truncate没有返回值
   delete可以返回受影响的行数
5. truncate不可以回滚
   delete可以回滚

## 6 TCL语言

### 6.1 数据库事务

含义：

事务：一条或多条sql语句组成一个执行单位，一组sql语句要么都执行要么都不执行



特点（ACID）：

* A 原子性：一个事务是不可再分割的整体，要么都执行要么都不执行
* C 一致性：一个事务可以使数据从一个一致状态切换到另外一个一致的状态
* I 隔离性：一个事务不受其他事务的干扰，多个事务互相隔离的
* D 持久性：一个事务一旦提交了，则永久的持久化到本地

### 6.2 事务的分类

事务的创建：

* 隐式事务：事务没有明显的开启和结束的标记
  比如insert、update、delete语句

  `delete from 表 where id =1;`

* 显式事务：事务具有明显的开启和结束的标记
  前提：必须先设置自动提交功能为禁用

```mysql
#步骤1：开启事务
set autocommit=0;
start transaction;#可选的
#步骤2：编写事务中的sql语句(select insert update delete)
语句1;
语句2;
...

#步骤3：结束事务
commit; #提交事务
rollback;#回滚事务

savepoint #节点名;设置保存点
```

### 6.3 并发事务

1. 事务的并发问题是如何发生的？
   多个事务同时操作同一个数据库的相同数据时
2. 并发问题都有哪些？
   脏读：一个事务读取了其他事务还没有提交的数据，读到的是其他事务“更新”的数据
   不可重复读：一个事务多次读取，结果不一样
   幻读：一个事务读取了其他事务还没有提交的数据，只是读到的是 其他事务“插入”的数据
3. 如何解决并发问题
   通过设置隔离级别来解决并发问题
4. 隔离级别

|                  | 脏读 | 不可重复读 | 幻读 |
| ---------------- | ---- | ---------- | ---- |
| read uncommitted | ×    | ×          | ×    |
| read committed   | √    | ×          | ×    |
| repeatable read  | √    | √          | ×    |
| serializable     | √    | √          | √    |

## 7 其他

### 7.1 视图

mysql5.1版本出现的新特性，本身是一个虚拟表，它的数据来自于表，通过执行时动态生成。

1. 好处：
   * 简化sql语句

   * 提高了sql的重用性
   * 保护基表的数据，提高了安全性

2. 创建：

```mysql
create view 视图名
as
查询语句;
```

3. 修改
   方式一：

   ```mysql
   create or replace view 视图名
   as
   查询语句;
   ```

   方式二：

   ```mysql
   alter view 视图名
   as
   查询语句
   ```

4. 删除

   ```mysql
   drop view 视图1，视图2,...;
   ```

5. 查看

   ```mysql
   desc 视图名;
   show create view 视图名;
   ```

6. 使用
   插入`insert` ； 修改`update` ；删除`delete`  ；查看`select`
   注意：视图一般用于查询的，而不是更新的，所以具备以下特点的视图都不允许更新

   * 包含分组函数、group by、distinct、having、union、
   * join
   * 常量视图
   * where后的子查询用到了from中的表
   * 用到了不可更新的视图

   ```mysql
   #查询姓名中包含a字符的员工名、部门名和工种信息
   #①创建
   CREATE VIEW myv1
   AS
   
   SELECT last_name,department_name,job_title
   FROM employees e
   JOIN departments d ON e.department_id  = d.department_id
   JOIN jobs j ON j.job_id  = e.job_id;
   
   #②使用
   SELECT * FROM myv1 WHERE last_name LIKE '%a%';
   ```

7. 视图和表的对比

|      | 关键字 | 是否占用物理空间        | 使用         |
| ---- | ------ | ----------------------- | ------------ |
| 视图 | view   | 占用较小，只保存sql逻辑 | 一般用于查询 |
| 表   | table  | 保存实际的数据          | 增删改查     |

### 7.2 变量

### 7.3 存储过程和函数

### 7.4 流程控制结构

