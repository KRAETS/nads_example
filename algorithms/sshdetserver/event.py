from login import Login


class Event:
    'Class that models an event'

    def __init__(self, event_threshold):
        """Constructor for Event object.
        event_threshold determines how many logins occur befor an event is created
        """
        self.logins = []
        self.gfi = 0
        self.zn = 0
        self.threshHold = event_threshold
        self.control = True


    def __repr__(self):
        return "Event:" + str(self.logins) + " " + str(self.gfi) + " " + str(self.zn) + " " + str(self.threshHold)\
               + " " + str(self.control)

    def add_login(self, login):
        """Adds a login to the event"""
        if not isinstance(login, Login):
            return False
        self.logins.append(login)
        return True

    def get_logins(self):
        return self.logins

    def is_threshold_reached(self):
        """Checks if the threshold has been reached"""
        return len(self.logins) >= self.threshHold

    def calculate_gfi(self):
        """Calculates the Global Failure Index-amount of fails"""
        self.gfi = 0
        for login in self.logins:
            if login.get_status() is False:
                self.gfi += 1
        return self.gfi

    def calculate_zn(self, mu, k):
        """Calculates the cusum of the event according to parameters
        mu average of failed logins under normal circumstances
        k parameter to keep in negative area the mean
        """
        self.zn = self.gfi - mu - k
        return self.zn

    def get_control(self):
        """Returns whether the event is in control or not"""
        return self.control

    def set_control(self, control):
        """Sets the state of control.  False is an out of control"""
        self.control = control
        return


