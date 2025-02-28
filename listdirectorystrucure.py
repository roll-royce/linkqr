import os

def list_directory_structure(root_dir, output_file):
    with open(output_file, 'w') as f:
        for dirpath, dirnames, filenames in os.walk(root_dir):
            level = dirpath.replace(root_dir, '').count(os.sep)
            indent = ' ' * 4 * level
            f.write(f'{indent}{os.path.basename(dirpath)}/\n')
            subindent = ' ' * 4 * (level + 1)
            for filename in filenames:
                f.write(f'{subindent}{filename}\n')

if __name__ == "__main__":
    current_directory = os.getcwd()
    output_file = 'directory_structure.txt'
    list_directory_structure(current_directory, output_file)
    print(f'Directory structure saved to {output_file}')
