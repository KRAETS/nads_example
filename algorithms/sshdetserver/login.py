class Login:
    """Class that models a login attempt"""

    def __init__(self, status, client, host, user, protocol=None):
        self.status = status
        self.client = client
        self.host = host
        self.user = user
        self.protocol = protocol

    def __repr__(self):
        return "Login:" + str(self.status) + " " + self.client + \
               " " + self.host + " " + self.user

    def get_status(self):
        return self.status

    def get_protocol(self):
        return self.protocol

    def get_client(self):
        return self.client

    def get_host(self):
        return self.host

    def get_user(self):
        return self.user

    def set_status(self, status):
        self.status = status

    def set_client(self, client):
        self.client = client

    def set_host(self, host):
        self.host = host

    def set_user(self, user):
        self.user = user

    def set_protocol(self,prot):
        self.protocol = prot

    @staticmethod
    def parse_from_json(json):
        """Parses a login from a json string"""
        status = json["status"]
        client = json["client"]
        host = json["host"]
        user = json["user"]
        return Login(status, client, host, user)
