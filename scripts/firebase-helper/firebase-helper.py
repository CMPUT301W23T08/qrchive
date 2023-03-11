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
uofa_locations = [
    (53.52688442998371, -113.5261982539656),
    (53.52661337650271, -113.52739988356052),
    (53.52608401999693, -113.52344630761651),
    (53.52721288070093, -113.52469085255412),
    (53.52669309829482, -113.52566717660001),
    (53.52602343057731, -113.52796314743314),
    (53.528077044708, -113.52579592261375),
    (53.527703957627075, -113.5219710926583),
    (53.52725752430161, -113.51990579170709),
    (53.52568540379377, -113.52070508993573),
    (53.5245660694504, -113.52006135978074),
    (53.523475407528366, -113.51995943584048),
    (53.52233369040366, -113.52395056275303),
    (53.52323941264983, -113.5239505627968),
    (53.52580658350343, -113.52176188027401),
    (53.527324283280635, -113.52933562074229)
]

for location in uofa_locations:
    geoPoint = firestore.GeoPoint(location[0], location[1]);
    data = {
    u'date': u'03/01/23',
    u'hasLocation': True,
    u'hash': u'9054102b56fd7f26f1e864550a7e148af16564816a119e2fe2bfea0115c4467a',
    u'hashVal': u'1221',
    u'location': geoPoint,
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
