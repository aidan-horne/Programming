import requests
import pyodbc
from datetime import datetime

# RETURNS AN INT ERROR BUT WORKS.

url = "https://capellasupport.zendesk.com/api/v2/organizations.json"

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
                                orgId = var.get("id")
                                orgName = var.get("name")
                                domainName = ", ".join(var.get("domain_names"))
                                details = var.get("details")     

                                data = [isInt(orgId), orgName, domainName, details]
                                try:
                                    cursor.execute("""
                                        INSERT INTO ZEN_ORGS ([ORG_ID], [ORGNAME], [DOMAIN_NAMES], [DETAILS])
                                        VALUES (?, ?, ?, ?)
                                    """, data)

                                    connection.commit()
                                except pyodbc.IntegrityError:
                                    pass    
                        except TypeError:
                            print(var)

        except AttributeError:
            x = response.json()
            url = list(x.values())[1]