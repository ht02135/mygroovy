--
BEGIN TRANSACTION;

				--

Insert into users(user_name,first_name,last_name)
values('ht02135_xml','hung','tsai')
				

-- Display the results
USE world
SELECT * FROM users
FOR XML AUTO

ROLLBACK;

-- Clean up

PRINT 'Cleanup...'
DECLARE @table_name varchar(100);

BEGIN TRANSACTION;
SET @table_name = 'users';
PRINT 'Table: ' + @table_name + '...'
DBCC checkident(@table_name, RESEED, 0);
DBCC checkident(@table_name, RESEED);
COMMIT;

