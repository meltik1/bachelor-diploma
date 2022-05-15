import math
import json
import sys

def E(l, mu, v):
    a = l/mu
    znam = 1
    for i in range(1, v+1):
        znam += math.pow(a, i) / math.factorial(i)
    chisl = math.pow(a, v) / math.factorial(v)
    print("Доля потерянных заявок \n")
    print(chisl/znam)

def sigma(v, a):
    return a/v

def recursionE(r, a):
    e_from_prev = 1
    results = 1
    for i in range(1, r+1):
        results = a * e_from_prev/(r+a*e_from_prev)
        e_from_prev = results
    #
    # E_from_prev = recursionE(r-1, a)
    # result = a*E_from_prev/(r+a*E_from_prev)
    return results

def D(a,v, E):
    result = v * E / (v - a*(1 -E))
    return result

def get_channels_number_that_less_then(percent, l, average_time,  t):
    mu = 1/average_time

    a = l/mu
    channels = math.ceil(a)

    E = recursionE(channels, a)
    res = 1 - D(a, channels, E) * math.pow(math.e, -channels * mu * t * (1 - sigma(channels, a)))
    real_percentage = res
    while real_percentage < percent:
        channels += 1
        E = recursionE(channels, a)
        print("D equals: ", D(a,channels,E))
        res = 1 - D(a,channels, E)*math.pow(math.e, -channels*mu*t*(1-sigma(channels, a)))
        real_percentage = res
        print(real_percentage)



    return (channels, real_percentage)

if __name__ == '__main__':
    #Интенсивность поступления
    lambda_var = float(sys.argv[1])
    #Интенсивность обработки
    mu_var = float(sys.argv[2])

    #Время в очереди
    t = float(sys.argv[3])

    #Процент заявок которые находится в очереди не более T
    perc = float(sys.argv[4])

    results = dict()
    response = get_channels_number_that_less_then(perc, lambda_var, mu_var, t)
    results["channels"] = response[0]
    results["percentage"] = response[1]

    print(json.dumps(results))


