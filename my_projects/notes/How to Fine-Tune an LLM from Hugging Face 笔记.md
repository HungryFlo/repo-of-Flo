# How to Fine-Tune an LLM from Hugging Face? 笔记

使大模型适应特定领域的方法：fine-tuning微调、prompt engineering提示词工程、RAG检索增强生成。

## 大语言模型的微调

微调可以是全部微调，也可以是部分微调，由于大语言模型过于巨大，全部微调是不现实的，因此 Performance Efficient fine-tuning(PEFT) 是一种微调大语言模型的常用方法。

### 载入预训练的模型

Hugging Face 有很多有用的库和模块，例如 SFT, PEFT 和 AutoTokenizer。

在这里我们使用 Falcon-7b

```python
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM, BitsAndBytesConfig
from peft import LoraConfig

modelID = "tiiuae/falcon-7b"
```

> LORA 是一种低资源微调大模型的方法。

### 准备数据集

SFT 可以直接使用 Hugging Face 中的数据集，当然也可以自己上传。

```python
dataset = load_dataset("timdettmers/openassistant-guanaco", split="train")
```

### 根据需求来调整模型

除了可以使用部分微调的方法，我们还可以使用 quantization （神经网络量化）来减少参数量。

```python
quantizationConfig = BitsAndBytesConfig(
    load_in_4bit=True,
    bnb_4bit_compute_dtype=torch.float16,
    bnb_4bit_quant_type="nf4"
)

model = AutoModelForCausalLM.from_pretrained(modelID, quantization_config=quantizationConfig)
```

然后导入 tokenizer

```python
tokenizer = AutoTokenizer.from_pretrained(modelID)
tokenizer.add_special_tokens({'pad_token': '<PAD>'})
```

### 微调模型

初始化 trainer：

```python
trainer = SFTTrainer(
        model=model,
        train_dataset=dataset,
        dataset_text_field="text",
        max_seq_length=512,
        tokenizer=tokenizer,
        packing=True,
    )
```

好啦，可以开始 train 啦：

```python
trainer.train()
```

训练完成之后，我们可以通过一些推理任务来进行测试。

使用`transformers`库中的`pipeline`功能来生成文本。这里的`pipeline`是一个预先训练好的模型，用于自动完成特定的任务，例如文本生成。

1. 首先，导入`transformers`库中的`pipeline`函数，并创建一个新的`pipeline`对象。这个对象被配置为执行文本生成任务，使用一个预先指定的模型和分词器（tokenizer）。

   ```python
   from transformers import pipeline 
   pipeline = pipeline(    
       "text-generation",
       model=model,
       tokenizer=AutoTokenizer.from_pretrained(model),
       device_map="auto", ) 
   ```

   在这里，`model`是你想要使用的预训练模型的名称或路径，`AutoTokenizer.from_pretrained(model)`是用于该模型的自动分词器。

2. 接下来，使用`pipeline`对象生成文本。这是通过调用`pipeline`对象并传递一个初始文本提示来完成的。这个提示是一个字符串，模型将基于这个提示生成续写的文本。

   ```python
   sequences = pipeline(    
       "Arguably, the most delicious fruit on this planet is cashew (in raw form). Found in Brazil and other tropical regions, its taste is unparalleled. What do you think, Sam? \\n Sam:",
       max_length=200,
       do_sample=True,
       top_k=10,
       num_return_sequences=1 
   ) 
   ```

   在这个例子中，`max_length=200`指定生成文本的最大长度，`do_sample=True`表示在生成文本时会进行采样，`top_k=10`限制了每一步采样的候选项数量，`num_return_sequences=1`表示只生成一个续写序列。

3. 最后，遍历生成的文本序列，并打印出来。

   ```python
   for seq in sequences:    
       print(f"Result: {seq['generated_text']}") 
   ```

   这段代码会输出模型基于给定提示生成的文本。在这个例子中，模型会尝试续写关于“cashew”这个话题的文本，并且假设有一个名叫Sam的人参与对话。

## 大语言模型微调的现实应用

RAG需要持续性进行投入，但是fine-tuning可以利用开源的大语言模型，“一次投入，终身受益”，非常nice。

1. 用户服务自动化
2. 翻译服务
3. 个性化教育

## 原文链接

[How to Fine-Tune an LLM from Hugging Face (myscale.com)](https://myscale.com/blog/how-to-fine-tune-llm-from-huggingface/)