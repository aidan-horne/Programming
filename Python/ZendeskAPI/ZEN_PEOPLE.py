import requests
import pyodbc
from datetime import datetime

# RETURNS AN INT ERROR BUT WORKS.

url = "https://capellasupport.zendesk.com/api/v2/users.json"

headers = {
    "Content-Type": "application/json",
}

auth = ("aidan.horne@capellaconsulting.co.nz/token", "XHUm0W0pXTa2Ma7kkWdAWw5hYiHElbg6Pu1nUCAO")

# Construct connection string
connection_string = connection_string = 'DRIVER={SQL Server};SERVER=CapDBServer;DATABASE=capella;UID=aidanh;PWD=Quack1nce4^'

# Establish connection
connection = pyodbc.connect(connection_string)
cursor = connection.cursor()

def isInt(var):
    try:
        int(var)
        return var
    except TypeError:
        return None

def dictToString(var):
    result_string = ""
    for key, value in var.items():
        result_string += f"{key}: {value}, "
    
    return result_string

# Create a session for more verbose output
while url is not None:
    with requests.Session() as session:
        session.headers.update(headers)
        session.auth = auth
        response = session.get(url)
        try:
            if response.status_code == 200:
                # Successful request
                data = response.json()
                for result in data.values():
                    if result is not None:
                        try:
                            for var in result:

                                pid = var.get("id")
                                name = var.get("name")
                                email = var.get('email')
                                createdDate = var.get("created_at")
                                phone = var.get("phone")
                                role = var.get("role")
                                verified = var.get("verified")    

                                if createdDate is not None:
                                    dateCObject = datetime.strptime(createdDate, "%Y-%m-%dT%H:%M:%SZ")
                                else:
                                    dateCObject = None

                                data = [pid, name, email, dateCObject, phone, role, verified]
                                try:
                                    cursor.execute("""
                                        INSERT INTO ZEN_PEOPLE ([PID], [NAME], [EMAIL], [CREATEDATE], [PHONE], [ROLE], [VERIFIED])
                                        VALUES (?, ?, ?, ?, ?, ?, ?)
                                    """, data)

                                    connection.commit()
                                except pyodbc.IntegrityError:
                                    pass    
                        except TypeError:
                            print(var)

        except AttributeError:
            x = response.json()
            url = list(x.values())[1]
            print(url)
