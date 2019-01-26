import io
import os

# Imports the Google Cloud client library
from google.cloud import vision
from google.cloud.vision import types

# define numerical values for likelihood scale
likelydict = {'VERY_UNLIKELY' : -2, 'UNLIKELY' : -1, 'POSSIBLE' : 0, 'LIKELY' : 1, 'VERY_LIKELY' : 2}

# Instantiates a client
client = vision.ImageAnnotatorClient()

# The name of the image file to annotate
file_name = os.path.join(
    os.path.dirname(__file__),
    './images/test4.jpg')


def getfacedata(face_file, max_results=4):
    """Uses the Vision API to detect faces in the given file.
    Args:
        face_file: A file-like object containing an image with faces.
    Returns:
        An array of Face objects with information about the picture.
    """
    # [START vision_face_detection_tutorial_client]
    client = vision.ImageAnnotatorClient()
    # [END vision_face_detection_tutorial_client]

    content = face_file.read()
    image = types.Image(content=content)

    return client.face_detection(image=image, max_results=max_results).face_annotations

def getlabeldata(image_file):
  content = image_file.read()
  image = types.Image(content=content)
  # Performs label detection on the image file
  response = client.label_detection(image=image)
  return response



labeldict = {}
# Loads the image into memory
with io.open(file_name, 'rb') as image_file:
    labeldata = getlabeldata(image_file)
for label in labeldata.label_annotations:
  labeldict[label.description] = label.score


with io.open(file_name, 'rb') as image_file:
    facesdata = getfacedata(image_file, 6)
    totaljoyscore = 0
    for i in range(len(facesdata)):
      face = facesdata[i]
      l = face.joy_likelihood
      if (not l == 0):
        joyscore = ((l - 3)*face.detection_confidence)
        totaljoyscore = totaljoyscore + joyscore
      #face = facesdata[i]
      #face['detection_confidence']*face['joy_likelihood']

print({'labels':labeldict, 'joyscore':totaljoyscore})


