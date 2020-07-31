import tensorflow as tf

converter = tf.lite.TFLiteConverter.from_saved_model('Inflated 3D', tags=[[], ['train']])
converter.target_spec.supported_ops = [tf.lite.OpsSet.SELECT_TF_OPS]
converter.optimizations = [tf.lite.Optimize.DEFAULT]                                  
tflite_model = converter.convert()
open("converted_model2.tflite", "wb").write(tflite_model)