package orangeHRM.model;

public class Employee {
	private String firstName;
	private String middleName;
	private String lastName;
	private String employeeId;
	private String username;
	private String password;

	public Employee(String firstName, String middleName, String lastName,
					String employeeId, String username, String password) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.employeeId = employeeId;
		this.username = username;
		this.password = password;
	}

	// Getters and Setters
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }

	public String getMiddleName() { return middleName; }
	public void setMiddleName(String middleName) { this.middleName = middleName; }

	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

	public String getEmployeeId() { return employeeId; }
	public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
}