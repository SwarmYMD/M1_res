from PIL import Image
 
imgs = []                                                   # 画像をappendするための空配列を定義
imgs2 = []

# ファイルのフルパスからファイル名と拡張子を抽出
for i in range(1,51):
    img = Image.open('./pics_survey/expPher/step'+ str(i) +'.png')                          # 画像ファイルを1つずつ開く
    imgs.append(img)                                        # 画像をappendで配列に格納していく


# appendした画像配列をGIFにする。durationで持続時間、loopでループ数を指定可能。
imgs[0].save('./gif/expPher.gif',
                save_all=True, append_images=imgs[1:], optimize=False, duration=600, loop=0)

for i in range(1,51):
    img2 = Image.open('./pics_survey/disPher/step'+ str(i) +'.png')                          # 画像ファイルを1つずつ開く
    imgs2.append(img2)                                        # 画像をappendで配列に格納していく

imgs2[0].save('./gif/disPher.gif',
                save_all=True, append_images=imgs2[1:], optimize=False, duration=600, loop=0)