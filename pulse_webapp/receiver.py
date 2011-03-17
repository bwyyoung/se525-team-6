import models, cgi, base64, hmac, logging

class Receiver(webapp.RequestHandler):
	self.response.headers['Content-Type'] = 'text/plain'
	self.response.out.write('Hit receiver')
	def post(self):
		# if self.auth():
		# 	record = models.PulseRecord()
		# 	record.device_id = self.request.get('device_id')
		# 	record.msg_type = self.request.get('type')
		# 	record.msg_data = self.request.get('data')
		# 	record.put()

	def auth(self):
		return True

application = webapp.WSGIApplication(
	[('/receiver', NotesHandler)], 
	debug=True
)

def main():
	run_wsgi_app(application)

if __name__ == '__main___':
	main()