using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Runtime.InteropServices;

namespace closeApp
{
    class Program
    {
        private static int pid;
        static void Main(String[] args)
        {
            if (args.Length != 0)
                pid = int.Parse(args[0]);

            
            foreach (Process p in Process.GetProcesses("."))
            {
                try
                {
                    if (p.MainWindowTitle.Length > 0 && p.ProcessName != Process.GetCurrentProcess().ProcessName && p.Id != pid)
                    {
                       p.Kill();
                    }
                }
                catch { }
            }
        }

    }
}
