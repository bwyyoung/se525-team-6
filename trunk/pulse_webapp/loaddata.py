import logging
import models

from google.appengine.ext import webapp
from google.appengine.ext.webapp import util
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

class DataHandler(webapp.RequestHandler):
	def get(self):
		#PROFILE
		# profile = models.PulseProfile()
		# profile.first_name = "Optimus"
		# profile.last_name = "Prime"
		# profile.pass_hash = "abcd"
		# profile.device_id = 1
		# profile.device_lost = False
		# profile.wipe_data = False
		# profile.is_wiped = False
		# ident = profile.put()

		#RECORD
		record = models.PulseRecord()
		record.device_id = 1
		record.msg_type = "gps"
		record.msg_data = "{'lat':25.00, 'long':15.00}"
		record.put()
		self.response.out.write('Created Record ')

def main():
	run_wsgi_app(application)

application = webapp.WSGIApplication(
	[('/loadDefData', DataHandler)], 
	debug=True
)

if __name__ == '__main__':
	main()