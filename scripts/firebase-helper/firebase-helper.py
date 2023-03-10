import firebase_admin
import hashlib
from firebase_admin import firestore, credentials

app = firebase_admin.initialize_app(credentials.Certificate("key.json"))
db = firestore.client()

## Show all ScannableCodes
# for item in db.collection(u'ScannableCodes').stream():
    # print(item.to_dict())

# Add data to firebase
# data = {
# u'code': u'this is cmput 301',
# u'code_type': u'qrcode',
# u'hash': u'9054102b56fd7f26f1e864550a7e148af16564816a119e2fe2bfea0115c4467a'
# }
# db.collection(u'ScannableCodes').document().set(data)

# Add a specific GeoPoint QR Code
location = firestore.GeoPoint(53.4519, -113.5857);
data = {
u'date': u'03/01/23',
u'hasLocation': True,
u'hash': u'9054102b56fd7f26f1e864550a7e148af16564816a119e2fe2bfea0115c4467a',
u'hashVal': u'1221',
u'location': location,
u'locationImage': u'placeholder_name.png',
u'userDID': u'D9vkTy1EoQfULzVhcytb',
}
db.collection(u'ScannedCodesTest').document().set(data)

# text_list = ["1", "2", "3", "4"]
# for text in text_list:
#     data = {
#     u'code': text,
#     u'code_type': u'qrcode',
#     u'hash': hashlib.sha256(text.encode()).hexdigest()
#     }
#     db.collection(u'ScannableCodes').document().set(data)

# Add a duplicate documdent from a collection to the same collection
# collection_ref = db.collection("ScannedCodes")

# Query the collection to get the first document
# query = collection_ref.limit(1).stream()
# doc = next(query)

# Create a copy of the document
# doc_data = doc.to_dict()
# collection_ref.add(doc_data)
