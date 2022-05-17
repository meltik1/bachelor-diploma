import PySimpleGUI as sg
import requests as req
import grequests as greq
import time as timelib
import json
import random

url = "http://localhost:8080"
def get_statistics(url):
    target = url + "/order/statistics"
    headers = {"Content-Type": "application/json; charset=utf-8"}
    result = req.get(target, headers=headers)
    result_json = json.loads(result.text)

    window["stat_1"].update('{:.3f}'.format(float(result_json["averageProcessing"])))
    window["stat_2"].update('{:.3f}'.format(float(result_json["requestsIntensivity"])))

    window["stat_3"].update(result_json["channelsNumber"])



def exception_handlerr(request, exception):
    print("Request failed", request.url)


def send_requests(url, average_insevity, time):
    target_url = url + "/order"
    body = dict()
    body["address"] = "Hey"
    body["destinationToAddress"] = random.randint(0, 1000)
    body["userId"] = 1
    body["companyId"] = 1

    headers = {"Content-Type" : "application/json; charset=utf-8"}
    while time >= 0:
        start = timelib.time()
        #res = req.post(target_url, data=json.dumps(body), headers=headers)
        rs = [greq.post(target_url, data=json.dumps(body), headers=headers) for i in range(int(average_insevity))]
        for r in greq.map(rs, size=16, exception_handler=exception_handlerr):
            print(r.status_code, r.url)
        if (timelib.time() - start) < 1:
            print("Here")
            timelib.sleep(0.5)
        time -= 1



sg.theme('DarkAmber')
# Устанавливаем цвет внутри окна
length = 35
layout = [  [sg.Text('Введите url координатора',  size=(length, 1)), sg.InputText(key="url", default_text=url)],
            [sg.Text('Введите интенсивность запросов', size=(length, 1)), sg.InputText(key="intense", default_text=30), sg.Text('ед/cек.')],
            [sg.Text('Среднее время обработки запроса', size=(length, 1)), sg.Text(size=(12,1), key='stat_1'),sg.Text('мсек')  ],
            [sg.Text("Интесивность входного потка запросов", size=(length, 1)), sg.Text(size=(12,1), key='stat_2'),sg.Text('eд/сек.')],
            [sg.Text('Необходимое число логических процессоров', size=(length, 1)), sg.Text(size=(12,1), key='stat_3'),sg.Text('ед.')  ],
            [sg.Button('Отправить запросы'), sg.Button('Отмена'), sg.Button('Получить статистику')] ]

# Создаем окно
window = sg.Window('Метод определения оптимального количества вычислительных ресурсов', layout)
# Цикл для обработки "событий" и получения "значений" входных данных
while True:
    event, values = window.read()
    if event == sg.WIN_CLOSED or event == 'Отмена':
# если пользователь закрыл окно или нажал «Отмена»
        break
    if event == 'Отправить запросы':
        send_requests(values["url"], values["intense"], 30)
    if event == 'Получить статистику':
        get_statistics(values["url"])

window.close()