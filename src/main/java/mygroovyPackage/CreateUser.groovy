package mygroovyPackage

@Grapes([
        @Grab(group = 'org.apache.commons', module = 'commons-io', version = '1.3.2'),
        @Grab(group = 'commons-io', module = 'commons-io', version = '2.12.0'),
        @Grab(group = 'commons-lang', module = 'commons-lang', version = '2.4'),
        @Grab(group = 'org.apache.poi', module = 'poi-ooxml', version = '5.2.5'),
        @Grab(group = 'net.sourceforge.jtds', module = 'jtds', version = '1.2.7'),
])
import groovy.sql.Sql
import org.apache.commons.lang.*
import org.apache.poi.ss.usermodel.*

/*
cd C:\worksplace\mygroovy\src\main\java\mygroovyPackage
groovy CreateUser.groovy
*/
class CreateUser{

	def usersFile="./UserGen.xlsx"
	def userPasswd="xyz"
	def jdbcUserName="root"
	def jdbcPassword="ZAQ!zaq1"

	public CreateUser(){}

	static void main( args){
		def me = new CreateUser()
		me.exec(args)
	}

	public void exec(args) {
		File file = new File(usersFile)
		if (file.exists()) {
			println "FOUND and Reading File ${this.usersFile} to Create Users"
		} else {
			println "${this.usersFile} NOT FOUND"
		}
		
		def userName = null
		def firstName = null
		def lastName = null
		def action = null

		/*
Workbook aWorkBook = Workbook.getWorkbook(new File("C:\\Users\\Response.xls"));     
WritableWorkbook workbook1 = Workbook.createWorkbook(new File("C:\\Users\\Responses.xls"), aWorkBook);
		*/
		InputStream is = new FileInputStream(file)
		Workbook wb = WorkbookFactory.create(is)
		Sheet sheet = wb.getSheetAt(0)
		Iterator<Row> rowIt = sheet.rowIterator()

		while(rowIt.hasNext()) {
			try{
				Row row = rowIt.next() 			// technically i should use def since i dont care about type
				int rowId = row.getRowNum()	
				println "processing record ${rowId}"
		 
				if(rowId > 0) {
					Cell userNameCell = row.getCell(0)
					Cell firstNameCell = row.getCell(1)
					Cell lastNameCell = row.getCell(2)
					Cell actionCell = row.getCell(3)
							 
					if(userNameCell!=null)
						userName = userNameCell.getStringCellValue()
					else
						userName = null
						
					if(userName!=null && userName!='null') {
						
						if(firstNameCell!=null)
							firstName = firstNameCell.getStringCellValue()
						else
							firstName=null
		  
						if(lastNameCell!=null)
							lastName = lastNameCell.getStringCellValue()
						else
							lastName = null

						if(actionCell!=null)
							action = actionCell.getStringCellValue()
						else
							action="abort"
							
						println "userName:${userName} firstName:${firstName} lastName:${lastName} action:${action}"
								 
						if(action == "insert"){
							println "!!!Create User :${userName}"
							createUser(userName,firstName,lastName)
						} else if (action == "update"){
							println "!!!Update User :${userName}"
							updateUser(userName,firstName,lastName)
						} else if(action == "delete"){
							println "!!!Delete User :${userName}"
							deleteUser(userName,databaseHost,database)
						} else if(action == "abort"){
							println "!!!Abort User :${userName}"
						}
					}
				} //if	
			}catch(Exception e) {
				println "Issue while processing "
				println "exception e:${e}"
			}
		} //while
	} // exec
	
	////////////////////////////////////////////////////
		
	void createUser(String userName,String firstName,String lastName){
		println "called createUser"
		
		//db.driver=com.mysql.jdbc.Driver
		//db.url=jdbc:mysql://localhost:3306/weather
		//db.username=root
		//db.password=admin
		//db.dialect=org.hibernate.dialect.MySQLDialect
		def sql
		def jdbcDriver = 'com.mysql.jdbc.Driver'
		def dbHostname = "localhost"
		def dbPort     = "3306"
		def dbName     ='world'
		def errorCode = 0

		println "Creating User for ${userName} for DB : ${dbHostname}.${dbName}"
		 
		String userInsertQuery="""
			|Insert into users(user_name,first_name,last_name)
			|values('${userName}','${firstName}','${lastName}')
			""".stripMargin("|")
		 
		try{
			//jdbc:mysql://host1:33060/sakila
			//jdbc:mysql://localhost:3306/Peoples?autoReconnect=true&useSSL=false;
			//jdbc:mysql://localhost:3306/Peoples?verifyServerCertificate=false&useSSL=true
			def jdbcUrl = "jdbc:mysql://${dbHostname}:${dbPort}/${dbName}?autoReconnect=true&useSSL=false"
			sql = Sql.newInstance(jdbcUrl,jdbcUserName,jdbcPassword,jdbcDriver)
			
			//Insert User
			sql.execute(userInsertQuery)
		}catch(Exception e){
			errorCode = -1
			println "Exception :${e}"
		}finally{
			if(sql!=null)
			sql.close()
		}	
	}

	void updateUser(String userName,String firstName,String lastName){
		println "called updateUser"
	}
	
	void deleteUser(String userName,String firstName,String lastName){
		println "called deleteUser"
	}
}

////////////////////////////////////////////////////

/*
|Insert into users(user_name,first_name,last_name)
|values('${userName}','${firstName}','${lastName})
			
USE world;
CREATE TABLE users (
 id INT AUTO_INCREMENT PRIMARY KEY,
 user_name VARCHAR(50) NOT NULL,
 first_name VARCHAR(50) NOT NULL,
 last_name VARCHAR(50) NOT NULL
)	

USE world;
Insert into users(user_name,first_name,last_name)
values('wt02135','wan','tsai')
*/