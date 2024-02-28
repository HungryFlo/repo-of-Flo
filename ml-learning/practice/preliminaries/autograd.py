import matplotlib.pylab as plt
import numpy as np
from d2l import torch as d2l
import torch

x = np.linspace(-3 * np.pi, 3 * np.pi, 100)
x1 = torch.tensor(x, requires_grad=True)
y = torch.sin(x1)
y.backward()
x1.grad

d2l.plot()