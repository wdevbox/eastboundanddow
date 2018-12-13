import requests
import plotly.plotly as ply
import plotly
import plotly.graph_objs as go
prefix = "https://api.iextrading.com/1.0"
suffix = "/stock/DIA/batch?"
params = "types=chart&range=1m&last=1"
response = requests.get(prefix+suffix+params)
#print(response.status_code)
#text = response.text.replace("{", "")
#text = text.replace("}", "")

#text = text.replace(" ", "")
text = response.text.replace(",", "\n")
text = text.replace('"', "")
text = text.replace("{", "")
text = text.replace("[", "\n")
print(text)
new_text = ""
closes = list()
dates = list()
for line in text.split("\n"):
    if(("latestUpdate" not in line)&(("minute:" in line) | ("close:" in line) | ("label:" in line))):
        new_text+=line+"\n"
        if("close" in line):
            closes.append(line.split(":")[1])
        if("label" in line):
            dates.append(line.split(":")[1])

float_closes = list()
i = 0
for item in closes:
    float_closes.append(float(item))
rang = list()
for j in range(len(closes)):
    rang.append(j)
plotly.offline.plot({
    "data": [go.Scatter(x=rang,y=float_closes)],
    "layout": go.Layout(title="Eastbound and DOW: 30 day DOW Stock")
}, auto_open=True)
print("\n\n\n\n\n\n\n")
print(new_text)
print(closes)
print(len(closes))
print(dates)
print(len(dates))
