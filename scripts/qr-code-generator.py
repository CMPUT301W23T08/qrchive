# Firstly, do pip install qrcode

import qrcode, string, random

# img = qrcode.make('Some data here')
# img.save("some_file.png")

def random_string(n : int) -> str:
    return ''.join(random.choice(string.ascii_letters) for i in range(n))

for i in range(100):
    name = random_string(32)
    img = qrcode.make(name)
    img.save("../qr-codes/" + name + ".png")
