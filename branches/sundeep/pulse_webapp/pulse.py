import logging, os

from models import PulseRecord
from google.appengine.ext import webapp
from google.appengine.ext.webapp import util
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db
from google.appengine.ext.webapp import template

class PulseRenderer(webapp.RequestHandler):
	def get(self):
		gql = "SELECT * FROM PulseRecord ORDER BY record_date DESC"
		records = db.GqlQuery(gql)
		template_values = {
			"records": records,
			"last_checkin": records[0].record_date
		}
		path = os.path.join(os.path.dirname(__file__), 'templates/pulse.html')
		self.response.out.write(template.render(path, template_values))

def main():
	run_wsgi_app(application)

#define application
application = webapp.WSGIApplication(
	[('/pulse', PulseRenderer)], 
	debug=True
)

if __name__ == '__main__':
	main()
