import logging, md5, os

from google.appengine.ext import webapp
from google.appengine.ext.webapp import util
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db
from google.appengine.ext.webapp import template

class LoginHandler(webapp.RequestHandler):
	def get(self):
		template_values = {}
		path = os.path.join(os.path.dirname(__file__), 'templates/login.html')
		self.response.out.write(template.render(path, template_values))

	def post(self):
		#process login creds, if authed, redirect
		if self.authorize(self.request.get('phone'), self.request.get('password')):
			self.redirect("/pulse")
		else:
			self.response.out.write("else")
	def authorize(self):
		return True
	# def authorize(self, phone, password):
	# 	h = hashlib.md5(password).hexdigest()
	# 	gql  ="SELECT * FROM PulseProfile WHERE device_id = " + phone + " AND password = '" + password "'"
	# 	res = db.GqlQuery(gql)
	# 	if res.count() == 0:
	# 		return True
	# 	else:
	# 		return False

def main():
	run_wsgi_app(application)

#define application
application = webapp.WSGIApplication(
	[('/login', LoginHandler)], 
	debug=True
)

if __name__ == '__main__':
	main()
