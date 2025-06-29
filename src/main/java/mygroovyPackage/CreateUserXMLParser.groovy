/*
launch dos command

echo %GROOVY_HOME%
echo %PATH%
set GROOVY_HOME=C:\apps\groovy-4.0.26
set PATH=C:\apps\groovy-4.0.26\bin;%PATH%
echo %GROOVY_HOME%
echo %PATH%

C:\worksplace\mygroovy\src\main\java\mygroovyPackage
groovy CreateUserXMLParser.groovy
*/

package mygroovyPackage

@Grapes([
        @Grab(group = 'org.apache.commons', module = 'commons-io', version = '1.3.2'),
        @Grab(group = 'commons-io', module = 'commons-io', version = '2.12.0'),
        @Grab(group = 'commons-lang', module = 'commons-lang', version = '2.4'),
        @Grab(group = 'org.apache.poi', module = 'poi-ooxml', version = '5.2.5'),
        @Grab(group = 'net.sourceforge.jtds', module = 'jtds', version = '1.2.7'),
])
import groovy.sql.Sql
import groovy.util.XmlParser
import org.apache.commons.lang.*
import org.apache.poi.ss.usermodel.*

class CreateUserXMLParser{

	def usersFile="./UserGen.xlsx"
	def usersXMLFile="./UserGen.xml"
	def sqlscriptName="./UserGen.sql"
	def userPasswd="xyz"
	def jdbcUserName="root"
	def jdbcPassword="ZAQ!zaq1"

	public CreateUserXMLParser(){}

	static void main( args){
		def me = new CreateUserXMLParser()
		me.sanityCheck(args)
		me.exec(args)
	}
	
	public void sanityCheck(args) {
		def text = '''
			<list>
        		<technology>
            	<name>Groovy</name>
        		</technology>
			</list>
		'''
	
		def list = new XmlParser().parseText(text)
		if (list.technology.name.text() == 'Groovy') {
			println "XmlParser should be working"
		} else {
			println "XmlParser NOT working"
		}
	}

/*
<articles>
    <article>
        <title>Java 12 insights</title>
        <author id="1">
            <firstname>Siena</firstname>
            <lastname>Kerr</lastname>
        </author>
        <release-date>2018-07-22</release-date>
    </article>
</articles>
///////////////
    articles.'*'.size() == 4
    articles.article[0].author.firstname.text() == "Siena"
    articles.article[2].'release-date'.text() == "2018-06-12"
    articles.article[3].title.text() == "Java 12 insights"
    articles.article.find { it.author.'@id'.text() == "3" }.author.firstname.text() == "Daniele"
/////////////////
Accessing Elements
// access the root name, prints movies
println movies.name()
// Access all movie element, We gets a List of GPathResult
def allMovies = movies.movie
// prints Number of movies: 2
println "Number of movies: ${allMovies.size()}"
// Access the first movie
def movie = movies.movie[0]
// print movie type
println "Movie type: ${movie.type.text()}"

We can access attributes using @ notation as shown in example below
println movies.movie[0].@title

Filtering elements
// Find all movies in the 'Thriller' category
def thrillerMovies = movies.movie.findAll { it.type == 'War, Thriller' }
println "Number of thriller movies: ${thrillerMovies.size()}" 
thrillerMovies.each { println "Thriller book title: ${it.@title.text()}" }

// Find movies published after 2000
def recentMovies = movies.movie.findAll { it.year.text().toInteger() > 2000 }
println "Recent movie title: ${recentMovies[0].@title.text()}"
*/
	
/*
<?xml version = "1.0" encoding = "UTF-8"?>
<users>
    <user>
        <userName id="1" action="insert">ht02135_xml</userName>
        <fullName>
            <firstName>hung</firstName>
            <lastName>tsai</lastName>
        </fullName>
    </user>
    <user>
        <userName id="1" action="insert">ht02135_xml2</userName>
        <fullName>
            <firstName>hung2</firstName>
            <lastName>tsai2</lastName>
        </fullName>
    </user>
</users>
*/
	public void exec(args) {
		def userName = null
		def firstName = null
		def lastName = null
		def action = null		
				
		File file = new File(usersXMLFile)
		if (file.exists()) {
			println "FOUND and Reading File ${this.usersXMLFile} to Create Users"
			
			PrintWriter SQLPrintWriter = getPrintWriter(sqlscriptName)
			SQLPrintWriter.print """--
					|BEGIN TRANSACTION;
					|
				""".stripMargin("|")

/*
The big difference between XmlSlurper and XmlParser is that the Parser will create something 
similar to a DOM, while Slurper tries to create structures only if really needed and thus uses 
paths, that are lazily evaluated. For the user both can look extremely equal. The difference 
is more that the parser structure is evaluated only once, the slurper paths may be evaluated 
on demand. On demand can be read as "more memory efficient but slower" here. Ultimately it 
depends how many paths/requests you do. If you for example want only to know the value of 
an attribute in a certain part of the XML and then be done with it, XmlParser would still 
process all and execute your query on the quasi DOM. In that a lot of objects will be 
created, memory and CPU spend. XmlSlurper will not create the objects, thus save memory 
and CPU. If you need all parts of the document anyway, the slurper loses the advantage, 
since it will create at least as many objects as the parser would.

translate into
1>if you need to process entire xml like loading users, then go with XmlParser
2>if you just need to access certain part of xml, then XmlSlurper is the way
*/
			def users = new XmlParser().parse(file) //parseText(String text)
			def total = users.'*'.size()
			println "Number of users: ${total}"
			println "users.user[0].firstName.text(): ${users.user[0].firstName.text()}"
			
			users.user.each() {
				user ->
				println "user: ${user}"
				println "user.userName.text(): ${user.userName.text()}"
				println "user.'userName'.text(): ${user.'userName'.text()}"
				println "user.firstName.text(): ${user.firstName.text()}"
				println "user.'firstName.text()': ${user.'firstName'.text()}"
				
				userName = user.userName.text()
				firstName = user.firstName.text()
				lastName = user.lastName.text()
				action = user.userName.'@action'.text()
				
				if (action) {
					println "action=${action}"
				} else {
					action="abort"
					println "something wrong abort"
				}
				
				println "userName:${userName} firstName:${firstName} lastName:${lastName} action:${action}"
					
				if(action == "insert"){
					println "!!!Create User :${userName}"
					populateSQL(SQLPrintWriter, userName, firstName, lastName)
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
			} //loop-thru-each
			
			SQLPrintWriter.print """
					|
					|-- Display the results
					|USE world
					|SELECT * FROM users
					|FOR XML AUTO
					|
					|ROLLBACK;
					|
					|-- Clean up
					|
					|PRINT 'Cleanup...'
					|DECLARE @table_name varchar(100);
					|
					|BEGIN TRANSACTION;
					|SET @table_name = 'users';
					|PRINT 'Table: ' + @table_name + '...'
					|DBCC checkident(@table_name, RESEED, 0);
					|DBCC checkident(@table_name, RESEED);
					|COMMIT;
					|
					|""".stripMargin()
			SQLPrintWriter.flush()
		} else {
			println "${this.usersXMLFile} NOT FOUND"
		}
	}
	
	void populateSQL(PrintWriter SQLPrintWriter, String userName,String firstName,String lastName){
		SQLPrintWriter.print """--
					|
					|Insert into users(user_name,first_name,last_name)
					|values('${userName}','${firstName}','${lastName}')
				""".stripMargin("|")
	} //exec
	
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
	
	PrintWriter getPrintWriter(def fileName) {
		def out
		if(fileName) {
			out = new PrintWriter(new File(fileName))
		} else {
			//use STDOUT if fileName is not specified
			out = new PrintWriter(System.out, true)
		}
		return out
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

USE world;
select u.* from users as u
*/