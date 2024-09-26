import warnings
warnings.filterwarnings('ignore')
from ultralytics import YOLO

if __name__ == '__main__':
    #model = YOLO('yolov8-PPHGNetV2-X.yaml')
    #model = YOLO('yolov8.yaml')
    model = YOLO(r'E:\Aircraft_skin\ultralytics\cfg\models\Add\HGNetV2-l.yaml')
    #model.load('yolov8n.pt')
    #model.load('best.pt') # 是否加载预训练权重,科研不建议大家加载否则很难提升精度
    #model.load('runs/train/exp22/weights/best.pt')
    model.train(data=r'E:\Aircraft_skin\datasets\VOCdevkit\data.yaml',
                # 如果大家任务是其它的'ultralytics/cfg/default.yaml'找到这里修改task可以改成detect, segment, classify, pose
                cache=False,
                imgsz=640,
                epochs=1,
                single_cls=False,  # 是否是单类别检测
                batch=8,
                close_mosaic=10,
                workers=0,
                device='0',
                optimizer='SGD', # using SGD
                #resume='runs/train/exp22/weights/last.pt', # 如过想续训就设置last.pt的地址
                amp=True,  # 如果出现训练损失为Nan可以关闭amp
                project='runs/train',
                name='last_exp',
                )