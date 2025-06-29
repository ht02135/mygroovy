package mygroovyPackage

@Grab(group='commons-io', module='commons-io', version='1.3.2')
@Grab(group='commons-lang', module='commons-lang', version='2.4')
//@Grab('info.picocli:picocli:4.2.0')

//C:\apps\groovy-2.4.21
/*
PS C:\Users\ht021> java --version
java 24.0.1 2025-04-15
Java(TM) SE Runtime Environment (build 24.0.1+9-30)
Java HotSpot(TM) 64-Bit Server VM (build 24.0.1+9-30, mixed mode, sharing)
PS C:\Users\ht021> groovy --version
Groovy Version: 2.4.21 JVM: 24.0.1 Vendor: Oracle Corporation OS: Windows 11
 */

//import groovy.cli.picocli.CliBuilder
/*
 import org.apache.commons.cli.DefaultParser
 import org.apache.commons.cli.Options
 */

import org.apache.commons.cli.*
@Grab(group='commons-io', module='commons-io', version='1.3.2')
@Grab(group='commons-lang', module='commons-lang', version='2.4')
//@Grab('info.picocli:picocli:4.2.0')

//C:\apps\groovy-2.4.21
/*
PS C:\Users\ht021> java --version
java 24.0.1 2025-04-15
Java(TM) SE Runtime Environment (build 24.0.1+9-30)
Java HotSpot(TM) 64-Bit Server VM (build 24.0.1+9-30, mixed mode, sharing)
PS C:\Users\ht021> groovy --version
Groovy Version: 2.4.21 JVM: 24.0.1 Vendor: Oracle Corporation OS: Windows 11
 */

//import groovy.cli.picocli.CliBuilder
/*
 import org.apache.commons.cli.DefaultParser
 import org.apache.commons.cli.Options
 */

import org.apache.commons.cli.*

class MyGroovyTest {
	private Boolean alpha = false

	public MyGroovyTest() {
		// TODO Auto-generated constructor stub
	}

	static main(args) {
		def me = new MyGroovyTest()
		me.exec(args)
		
		// If x is true according to groovy truth return x else return y
		// x ?: y
		// x ? x : y  // Standard ternary operator.
	}

/*
cd C:\worksplace\mygroovy\src\main\java\mygroovyPackage
groovy MyGroovyTest.groovy -h
groovy MyGroovyTest.groovy -c cityName -s stateName -z 02135,11111
*/
	public exec(def args) {
	
		if (args.length == 0) {
			println("Please provide a name as a command-line argument.")
		} else {
			def name = args[0]
			println("Hello, $name!")
			args.each{println it}
		}

		def cli = new CliBuilder(
			usage: 'demoGroovyCliBuilder -c cityName -s stateName -z zipCodes',  
            header: '\nAvailable options (use -h for help):\n',  
            footer: '\nInformation provided via above options is used to generate printed string.\n')  

		cli.with  
		{ 	
			h(longOpt: 'help',  required: false, 'Usage Information')
			c(longOpt: 'city',  required: true, args: 1, 'City Name')
			s(longOpt: 'state', required: true, args: 1, 'State Name')
			z(longOpt: 'zip',   required: true, args: Option.UNLIMITED_VALUES, valueSeparator: ',',  'Zip Codes (separated by comma)')
		
		}  
		
		def opt = cli.parse(args)
		if (!opt) {
			return
		} else {
			opt.each{println it}
		}

		def help = opt.h
		def cityName = opt.c
		def stateName = opt.s
		def zipCodes = opt.zs   // append 's' to end of opt.z to get more than first
		
		if (help) {
			cli.usage()
			System.exit(0)
		}
		
		if (cityName) {
			println "cityName : ${cityName}"
		}
		
		if (stateName) {
			println "stateName : ${stateName}"
		}
		
		if (zipCodes) {
			zipCodes.each
			{
			   print "zipCodes : ${it}"
			}
			println " "
		}
		
		language()
		
		testClosure()
		
	}
	
	//Type definitions are optional and actual types are determined at runtime
	public language() {
		def duck = new InnerDuck()
		
		def list = [duck]
		list.each { obj ->
			obj.display()
		}
		
		def universe = new InnerUniverse()
		universe.display()
	}
	
	public testClosure()
	{
		def printx = { name ->
			println name
		}
		
		def formatToLowerCaseClosure = { name ->
			return name.toLowerCase()
		}
		
		def calculate = {x, y, operation ->
			def result = 0
			switch(operation) {
				case "ADD":
					result = x+y
					break
				case "SUB":
					result = x-y
					break
				case "MUL":
					result = x*y
					break
				case "DIV":
					result = x/y
					break
			}
			println "calculate.result : ${result} "
		}
		
		//Varargs
		def addAll = {... args ->
			println "addAll.args.sum() : ${args.sum()} "
		}
		
		//A Closure as an Argument
		printx(formatToLowerCaseClosure("Hello! Closure"))
		printx(formatToLowerCaseClosure.call("Hello2! Closure2"))
		
		calculate(12, 4, "ADD")
		addAll(12, 10, 14)
	}
	
	public testCSVFileAcess() {
		
	}
	
	public testSpreadSheetFileAcess() {
	
    }
	
	public testDBAccess() {
/*
db.driver=com.mysql.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/weather
db.username=root
db.password=admin
db.dialect=org.hibernate.dialect.MySQLDialect
 */
	}
	
	private class InnerDuck {
		private getName() {
			'Duck'
		}
		
		public display() {
			println "InnerDuck.getName() : ${getName()} "
		}
	}
	
	private class InnerUniverse {
		private def name = 'test'
		private def list = ['Hello', 'World']
		
		public display() {
			def list2 = ['Hello2', 'World2']
			
			println "InnerUniverse.name : ${name}"
			
			list.each
			{
			   println "InnerUniverse.list : ${it}"
			}
			
			list2.each
			{
			   println "InnerUniverse.list2 : ${it}"
			}
		}
	}
}
