import os
import pty
import select
import shlex
import signal
import subprocess
import sys

def exit_handler(signum, frame):
    print("Triggered exit handler with signal {}".format(signum))
    if process.pid is None:
        print("Process PID is None")
        pass
    else:
        print("Killing child with pid {}".format(process.pid))
        process.terminate()
        process.kill()
        
def child_exit_handler(signum, frame):
    print("Child exited with pid {} ".format(process.pid))
    sys.exit(0)
    
def main(argv):
    if len(argv) < 1:
        print('usage: pseudotty.py <cmd>')
        sys.exit(2)

    sys.argv.pop(0)
    cmd = ' '.join(sys.argv)
    
    print('Opening pty')
    # Make sure stdin is a proper (pseudo) pty to avoid: tcgetattr errors
    (master, slave) = pty.openpty()
    global process
    process = subprocess.Popen(shlex.split(cmd), stdin=slave, stdout=slave, stderr=slave, close_fds=True)
    
    print('Registering exit handler')
    signal.signal(signal.SIGTERM, exit_handler)
    signal.signal(signal.SIGCHLD, child_exit_handler)
    
    if (sys.version_info > (3, 0)):
        # Python 3
        stdout = os.fdopen(master, 'rb', 0)
        while process.poll() is None:
            while stdout in select.select([stdout], [], [], 0)[0]:
                line = stdout.readline()
                if line:
                    print(" ".join(str(c) for c in line))
                    print(line.decode(sys.stdout.encoding).rstrip()+"")
                else:
                    break

        print("end")
    else:
        # Python 2
        while process.poll() is None:
            rlist, wlist, xlist = select.select([master], [], [])
            for f in rlist:
                output = os.read(f, 1000)
                sys.stdout.write(output)
                sys.stdout.flush()

        print("end")
    
if __name__ == '__main__':
    main(sys.argv[1:])

