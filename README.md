# OnlineML
Spring boot based RESTful service for machine learning

# Usage 
### Quick Start
In this quick start, you will know how to deploy and use the basic provided features. 
1.  To deploy, use command
    ```
    $ gradle bootRun
    ```
2.  Check `http://localhost:8080/swagger-ui.html` for swagger api, 
    a nice UI for sending requests to the server.
3.  First post your python script by 
    ```
    POST http://localhost:8080/file/hello.py
    ``` 
    with body as
    ```
    # hello.py
    import sys
    print(sys.version)
    ```
    Then you will get
    ```
     {
       "name": "hello.py",
       "id": 2,
       "lastModified": ...
     }
    ```
4.  Second, post a python runner profile attached with the id of `hello.py` you have seen in step 3, 
    which is `2` in this case. That is, 
    ```
    POST http://localhost:8080/pythonrunnerprofile/2
    ```
    then you will get
    ```
    {
      "id": 3,
      "state": "PREPARING",
      ...
      "pythonCode": {
        "requiredTimestamp": ...
        "fileInfo": {
          "name": "hello.py",
          "id": 2,
          "lastModified": ...
        }
      },
      ...
    }
    ```
    One thing you may want to know is the `state` entry, here is `PREPARING`.
    Beside `PREPARING`, other possible states are
    - `CREATED`: the python runner profile has been created
    - `PREPARING`: the server is preparing the environment
    - `RUNNING`: the server is running the python code
    - `CLEANING`: the python code is finished and the server is cleaning the environment
    - `FINISHED`: the whole process is finished
    - `CRASHED`: some thing went wrong, and you can check `errorMessages` entry for more information
5.  After a while, you can send request with the id of the python runner profile (e.g. 3 in this case),
    to see the current state by 
    ```
    GET http://localhost:8080/pythonrunnerprofile/info/3
    ```
    and you will get
    ```
    {
      "id": 3,
      "state": "FINISHED",
      "result": [
        {
          "name": "hello.py",
          "id": 6,
          "lastModified": ...
        }
      ],
      ...
      "summary": "3.7.3 (default, Mar 27 2019, 17:13:21) [MSC v.1915 64 bit (AMD64)]",
      ...
    }
    ```
    Once the state is `FINISHED`, you will see the stdout in `summary` entry 
    and all files left after the process in `result` entry.
### Run with dependencies
In most cases, you may want to use non-default packages. In this case, you need to set up an environment.
We use [Anaconda](https://www.anaconda.com/) as environment manager, 
and an `environment.yml`
would be needed to generate a proper environment.
You can see the [official manual](https://docs.conda.io/projects/conda/en/latest/user-guide/tasks/manage-environments.html#creating-an-environment-file-manually) 
for more detail to create the `environment.yml`. 
On the other hand, other dependencies may also be needed.
In the following demo, we will run `hello-2.py` with `abc.txt` under `python 2.7`.
```
# hello-2.py
import sys
print(sys.version)
with open('abc.txt') as fin, open('abc-copy.txt', 'w') as fout:
	for line in fin.readlines():
		fout.write(line)
```
```
# abc.txt
hello world
```
```
# env.yml
dependencies:
  - python=2.7
```
1.  `POST http://localhost:8080/anacondayaml/env.yml` with body as `env.yml`
2.  `POST http://localhost:8080/file/hello-2.py` with body as `hello-2.py`
3.  `POST http://localhost:8080/file/abc.txt` with body as `abc.txt`
4.  With `${env.yml}`, `${hello-2.py}` and `${abc.txt}` represent their id, 
    ```
    POST http://localhost:8080/pythonrunnerprofile/${hello-2.py}/?dependenciesIds%5B%5D=${abc.txt}&environmentId=${env.yml}
    ```
    to create a python runner profile.
5.  Finally, when the process finished, you will see something like
    ```
    {
      "id": 27,
      "state": "FINISHED",
      "result": [
        {
          "name": "abc-copy.txt",
          "id": 31,
          "lastModified": ...
        },
        {
          "name": "abc.txt",
          "id": 33,
          "lastModified": ...
        },
        {
          "name": "hello-2.py",
          "id": 32,
          "lastModified": ...
        }
      ],
      "environment": {
        "requiredTimestamp": ...,
        "fileInfo": {
          "name": "env.yml",
          "id": 13,
          "lastModified": ...
        }
      },
      "pythonCode": {
        "requiredTimestamp": ...,
        "fileInfo": {
          "name": "hello-2.py",
          "id": 26,
          "lastModified": ...
        }
      },
      "dependencies": [
        {
          "requiredTimestamp": ...,
          "fileInfo": {
            "name": "abc.txt",
            "id": 18,
            "lastModified": ...
          }
        }
      ],
      "summary": "2.7.16 |Anaconda, Inc.| (default, Mar 14 2019, 15:42:17) [MSC v.1500 64 bit (AMD64)]",
      ...
    }
    ```
    and you can gather your result by `GET http://localhost:8080/file/${abc-copy.txt}`.
# Dependencies
Use
```
$ gradle clean test
```
to test the dependencies.
## Common 
- `Java` (tested on `1.8.0_131`)
- `Gradle` (tested on `5.4.1`)
- `Anaconda` (tested on `conda 4.6.11`)

## Windows
- `openssl 32bit` (used by Anaconda) 

## Linux (not supported yet)
