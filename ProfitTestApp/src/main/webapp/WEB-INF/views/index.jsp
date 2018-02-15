<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
   <head>
   		<title>Profit Software Test Application</title>
   </head>
   
   <body>
      <b>Customers</b><br/>
      <table border="1">
      	<tr>
            <th>ID</td>
            <th>First name</td>
            <th>Last name</td>
            <th>Date of Birth</td>
            <th>Username</td>
            <th>Password</td>
        </tr>
      	<c:forEach items="${tableData}" var="row">
	  	<tr>
			<form action="updateCustomer" method="POST">      
        		<td><input type="text" name="id" value=${row[0]} readonly="readonly"/></td>
        		<td><input type="text" name="firstname" value=${row[1]} /></td>
        		<td><input type="text" name="lastname" value=${row[2]} /></td>
        		<td><input type="text" name="dateofbirth" value=${row[3]} /></td>
        		<td><input type="text" name="username" value=${row[4]} /></td>
        		<td><input type="text" name="password" value=${row[5]} /></td>
        		<td><input type="submit" name="action" value="Update"> <input type="submit" name="action" value="Delete"></td>
       		</form> 	
       	</tr>
	   	</c:forEach>
	   </table>
	   <br/>
	   <b>Add new customer: </b> <br/>
	   <form action="submitCustomer" method="POST">
  			First name:<br>
  			<input type="text" name="firstname"><br>
  			Last name:<br>
  			<input type="text" name="lastname"><br><br>
  			Date of Birth(yyyy-MM-dd):<br>
  			<input type="text" name="dateofbirth"><br><br>
  			Username:<br>
  			<input type="text" name="username"><br><br>
  			Password:<br>
  			<input type="text" name="password"><br><br>
  			<input type="submit" value="Submit">
		</form> 
   </body>
</html>