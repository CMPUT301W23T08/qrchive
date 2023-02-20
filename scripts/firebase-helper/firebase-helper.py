import firebase_admin
from firebase_admin import firestore, credentials

app = firebase_admin.initialize_app(credentials.Certificate("key.json"))
db = firestore.client()

for item in db.collection(u'ScannableCodes').stream():
    print(item.to_dict())
