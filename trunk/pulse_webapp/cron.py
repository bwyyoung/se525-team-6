from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

class CronHandler(webapp.RequestHandler):
	def get(self):
		self.deleteOldRecords()
	def deleteOldRecords():
		gql = "SELECT * FROM PulseRecord WHERE DATETIME(record_date) - DATE(NOW()) > 30"
		results = db.GqlQuery(gql).fetch(1000)
		db.delete(results)
		
#define application
application = webapp.WSGIApplication(
	[('/login', CronHandler)], 
	debug=True
)