import unittest

from event import Event
from login import Login

event_threshold = 10


class EventTestCase(unittest.TestCase):
    def setUp(self):
        self.event = Event(event_threshold)

    def test_event_threshold(self):
        for value in range(0, event_threshold):
            test_login = Login(False, "test", "test", "test")
            self.event.add_login(test_login)
            assert len(self.event.get_logins()) == value+1, 'incorrect size'
            if value < 9:
                assert self.event.is_threshold_reached() == False
        assert self.event.is_threshold_reached() == True



