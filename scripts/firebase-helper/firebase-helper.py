import firebase_admin
import hashlib
from firebase_admin import firestore, credentials

app = firebase_admin.initialize_app(credentials.Certificate("key.json"))
db = firestore.client()

def delete_codes_for_user(user_id):
    codes_ref = db.collection('ScannedCodes')
    query = codes_ref.where('userDID', '==', user_id)
    docs = query.stream()

    for doc in docs:
        doc.reference.delete()

def add_uofa_locations(user_id):
    
    uofa_locations = [ #taken from google maps
        (53.52688442998371, -113.5261982539656),
        (53.52661337650271, -113.52739988356052),
        (53.52608401999693, -113.52344630761651),
        (53.523475407528366, -113.51995943584048),
        (53.52233369040366, -113.52395056275303),
        (53.52323941264983, -113.5239505627968),
        (53.52580658350343, -113.52176188027401),
        (53.527324283280635, -113.52933562074229)
    ]
    
    for location in uofa_locations:

        my_loc = firestore.GeoPoint(location[0], location[1])
        my_date = firestore.SERVER_TIMESTAMP
        my_hash = hashlib.sha256(str(my_loc).encode()).hexdigest()
        my_hash_val = int(my_hash[-7:], 16)
        
        data = {
            u'date': my_date,
            u'hasLocation': True,
            u'hash': my_hash,
            u'hashVal': my_hash_val,
            u'location': my_loc,
            u'locationImage': u'placeholder_name.png',
            u'userDID': user_id,
        }

        # print(my_hash)
        # print(my_hash_val)

        db.collection(u'ScannedCodes').document().set(data)

if __name__ == "__main__":
    user_id = "sZL4FHQV3ZGKqjmLc3HM"
    add_uofa_locations(user_id)
    # delete_codes_for_user(user_id)
