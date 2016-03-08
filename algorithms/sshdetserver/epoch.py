from event import Event

class Epoch:
    'Class that models an attack epoch'

    def __init__(self, consecutive_ooc, history):
        self.consecutive_out_of_control = consecutive_ooc
        self.previous_history = history

    def add_out_of_control_event(self,event):
        if type(event) is not Event:
            raise TypeError("Type of event")
        self.consecutive_out_of_control.append(event)

    def add_previous_event(self, event):
        if type(event) is not Event:
            raise TypeError("Type of event")
        self.previous_history.append(event)

    def get_out_of_control_events(self):
        return self.consecutive_out_of_control

    def get_history_events(self):
        return self.previous_history