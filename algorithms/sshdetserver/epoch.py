from event import Event

class Epoch:
    """Class that models an attack epoch"""

    def __init__(self, consecutive_ooc, history):
        """Constructor.  Receives the events marked as out of control and the history leading up to it"""
        self.consecutive_out_of_control = consecutive_ooc
        self.previous_history = history

    def add_out_of_control_event(self, event):
        """Adds an out of control event to the epoch"""
        if type(event) is not Event:
            raise TypeError("Type of event")
        self.consecutive_out_of_control.append(event)

    def add_previous_event(self, event):
        """Adds an event to history"""
        if type(event) is not Event:
            raise TypeError("Type of event")
        self.previous_history.append(event)

    def get_out_of_control_events(self):
        """Returns the out of control events"""
        return self.consecutive_out_of_control

    def get_history_events(self):
        """Returns the history events"""
        return self.previous_history

    def __repr__(self):
        return "Epoch:" + str(self.consecutive_out_of_control) + "\n" + str(self.previous_history)