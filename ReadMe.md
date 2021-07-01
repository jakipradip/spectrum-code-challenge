ReadMe

This application was build as a coding exercise during interview process

Hosted on AWS EC2 (Will not be available after 2 weeks):
Some endpoints to try on the hosted applicaiton:
	1) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info
	2) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info/exportCsv
	3) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info?organization=Sandia%20National%20Laboratories%20(SNL)
	4) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info/exportCsv?organization=Sandia%20National%20Laboratories%20(SNL)
	5) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info?releaseCountGreaterThan=400
	6) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info/exportCsv?releaseCountGreaterThan=400
	7) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info?totalLaborHoursGreaterThan=1000
	8) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info/exportCsv?totalLaborHoursGreaterThan=1000
	9) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info?totalLaborHoursGreaterThan=1000&sortBy=releaseCount
	10) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info/exportCsv?totalLaborHoursGreaterThan=1000&sortBy=totalLaborHours
	11) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info?totalLaborHoursGreaterThan=1000&sortBy=releaseCount&sortOrder=asc
	12) http://ec2-52-42-93-178.us-west-2.compute.amazonaws.com/release/info/exportCsv?totalLaborHoursGreaterThan=1000&sortBy=totalLaborHours&sortOrder=dsc

Requirements:
1) Java 11 must have been installed in the computer


How to Run project?
1) First Clone this project into the computer
2) Go inside the folder
3) Make sure no other program is using port 8080 because this program will run on port 8080.
3) Run command in commandline or bash 
		$ ./gradlew bootRun
4) After running that command the application should be up in port 8080


Some Endpoints to try :

1) http://localhost:8080/release/info : This endpoint will return values in Json format 
2) http://localhost:8080/release/info/exportCsv :This endpoint will download a csv file with search results

Query Paramaters that can be used:
1)organization
	Example EndPoint : http://localhost:8080/release/info?organization=Sandia%20National%20Laboratories%20(SNL)
						http://localhost:8080/release/info/exportCsv?organization=Sandia%20National%20Laboratories%20(SNL)

2)releaseCountGreaterThan
	Example EndPoing : http://localhost:8080/release/info?releaseCountGreaterThan=400
						http://localhost:8080/release/info/exportCsv?releaseCountGreaterThan=400
						
						
3)totalLaborHoursGreaterThan
	Example EndPoing : http://localhost:8080/release/info?totalLaborHoursGreaterThan=1000
						http://localhost:8080/release/info/exportCsv?totalLaborHoursGreaterThan=1000

4)sortBy : This query paramater only works if we provide value to be "releaseCount" or "totalLaborHours"
	Example EndPoing : http://localhost:8080/release/info?totalLaborHoursGreaterThan=1000&sortBy=releaseCount
						http://localhost:8080/release/info/exportCsv?totalLaborHoursGreaterThan=1000&sortBy=totalLaborHours
						
5)sortOrder : expected values for sortOrder is either "asc" or "dsc"
	Example EndPoing : http://localhost:8080/release/info?totalLaborHoursGreaterThan=1000&sortBy=releaseCount&sortOrder=asc
						http://localhost:8080/release/info/exportCsv?totalLaborHoursGreaterThan=1000&sortBy=totalLaborHours&sortOrder=dsc
						
						
We can apply more than one query paramater at time as shown in above examples
	
