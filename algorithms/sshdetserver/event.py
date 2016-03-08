from login import Login


class Event:
    'Class that models an event'

    def __init__(self, event_threshold):
        self.logins = []
        self.gfi = 0
        self.zn = 0
        self.threshHold = event_threshold
        self.control = True


    def __repr__(self):
        return "Event:"+str(self.logins)+" "+str(self.gfi)+" "+ str(self.zn)+" "+str(self.threshHold)\
               +" "+str(self.control)

    def add_login(self, login):
        if not isinstance(login, Login):
            return False
        self.logins.append(login)
        return True

    def get_logins(self):
        return self.logins

    def is_threshold_reached(self):
        return len(self.logins) >= self.threshHold

    def calculate_gfi(self):
        for login in self.logins:
            if login.get_status() is False:
                self.gfi += 1
        return self.gfi

    def calculate_zn(self, mu, k):
        self.zn = self.gfi - mu - k
        return self.zn

    def get_control(self):
        return self.control

    def set_control(self, control):
        self.control = control
        return


