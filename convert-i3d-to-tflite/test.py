import numpy as np
import tensorflow as tf
from gluoncv.data.transforms import video
from gluoncv import utils
from gluoncv.utils.filesystem import try_import_decord
from mxnet import gluon, nd, image
decord = try_import_decord()

# Load TFLite model and allocate tensors.
interpreter = tf.lite.Interpreter(model_path="converted_model.tflite")
interpreter.allocate_tensors()

# Get input and output tensors.
input_details = interpreter.get_input_details()
print(input_details)
output_details = interpreter.get_output_details()
print(output_details)

url = './abseiling_k400.mp4'
video_fname = url
vr = decord.VideoReader(video_fname)
frame_id_list = range(0, 64, 2)
video_data = vr.get_batch(frame_id_list).asnumpy()
clip_input = [video_data[vid, :, :, :] for vid, _ in enumerate(frame_id_list)]
# Test model on random input data.
input_shape = input_details[0]['shape']
print(vr)
transform_fn = video.VideoGroupValTransform(size=224, mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
clip_input = transform_fn(clip_input)
clip_input = np.stack(clip_input, axis=0)
clip_input = clip_input.reshape((-1,) + (32, 3, 224, 224))
clip_input = np.transpose(clip_input, (0, 2, 1, 3, 4))
# print(clip_input)
input_data = nd.array(clip_input)
# print(input_data)
# print(input_data)
print("before set tensor")
interpreter.set_tensor(input_details[0]['index'], input_data)

print("before invoke")
interpreter.invoke()
print("after invoke")
output_data = interpreter.get_tensor(output_details[0]['index'])
print(output_data)

# # The function `get_tensor()` returns a copy of the tensor data.
# # Use `tensor()` in order to get a pointer to the tensor.
# output_data = interpreter.get_tensor(output_details[0]['index'])
# print(output_data)