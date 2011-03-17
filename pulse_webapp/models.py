from google.appengine.ext import db

class PulseRecord(db.Model):
	"""
	A PulseRecord represents a single record for a given device.
	Think of it like a blog post. It could be an image, gps data, text data,
	or an alert, or whatever other type of record you might be able 
	to come up with.
	"""
	device_id = db.IntegerProperty()
	msg_type = db.StringProperty()
	msg_data = db.StringProperty() #json?
	record_date = db.DateTimeProperty(auto_now_add=True)

class PulseProfile(db.Model):
	"""
	A PulseProfile represents a single user and device. 
	"""
	first_name = db.StringProperty()
	last_name = db.StringProperty()
	pass_hash = db.StringProperty()
	device_id = db.IntegerProperty()
	device_lost = db.BooleanProperty()
	wipe_data = db.BooleanProperty()
	is_wiped = db.BooleanProperty()
	last_update = db.DateTimeProperty()
	account_created = db.DateTimeProperty(auto_now_add=True)