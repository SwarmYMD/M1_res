from PIL import Image
import os
import glob
 
imgs = []                                                   # 画像をappendするための空配列を定義

# ファイルのフルパスからファイル名と拡張子を抽出
for i in range(51):
    img = Image.open('./pics_survey/image/step'+ str(i) +'.png')                          # 画像ファイルを1つずつ開く
    imgs.append(img)                                        # 画像をappendで配列に格納していく

# appendした画像配列をGIFにする。durationで持続時間、loopでループ数を指定可能。
imgs[0].save('./gif/survey.gif',
                save_all=True, append_images=imgs[1:], optimize=False, duration=600, loop=0)