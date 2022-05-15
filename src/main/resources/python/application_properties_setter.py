import os
import yaml
import sys

if __name__ == '__main__':
    path_to_application = sys.argv[1]
    number_of_threads = os.cpu_count()
    with open(path_to_application, "r") as stream:
        try:
            yaml_file = yaml.safe_load(stream)
            yaml_file['server']['tomcat']['threads']['min-spare'] = os.cpu_count()
            yaml_file['server']['tomcat']['threads']['max'] = os.cpu_count() + 6
        except yaml.YAMLError as exc:
            print(exc)
    with open(path_to_application, 'w') as file:
        documents = yaml.dump(yaml_file, file)