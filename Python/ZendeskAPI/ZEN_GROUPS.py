import requests
import pyodbc

url = "https://capellasupport.zendesk.com/api/v2/groups.json"

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

# Create a session for more verbose output
while url is not None:
    with requests.Session() as session:
        session.headers.update(headers)
        session.auth = auth

        response = session.get(url)
        if response.status_code == 200:
            # Successful request
            data = response.json()
            try:
                for result in data.values():
                    if result is not None:
                            for var in result:
                                groupId = var.get("id")
                                group = var.get("name")

                                data = [isInt(groupId), group]

                                try:
                                    cursor.execute("""
                                        INSERT INTO ZEN_GROUPS ([GROUP_ID], [GROUP])
                                        VALUES (?, ?)
                                    """, data)

                                    connection.commit()
                                except pyodbc.IntegrityError:
                                    cursor.close()
                                    connection.close()
                                    exit()
            except TypeError:
                pass