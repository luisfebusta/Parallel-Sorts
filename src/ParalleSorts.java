import java.util.Random;

public class ParalleSorts {

    public static void quickSort(Comparable [] a)
    {
        //shuffle input array to randomize input.
        //this prevents worse case scenario where the array is already sorted
        shuffle(a);
        int cores = Runtime.getRuntime().availableProcessors();
        
        quickSort(a,0,a.length-1, cores);
        
    }
    
    private static void quickSort(Comparable [] a, int start, int end, int cores)
    {
        if(start >= end)
            return;
        
        //pick 1st item as pivot
        int pivot = start;
        int i = start+1;
        int j = end;
        
        while (true)
        {
            while (a[i].compareTo(a[pivot]) <= 0)
            {
                i++;
                if (i >= end) break;
            }
            
            while (a[j].compareTo(a[pivot]) > 0)
            {
                j--; 
                if (j <= start) break;
            }
           
            if (i >= j)
            {
                break;
            }
            swap(a, i, j);
        }
        
        swap(a, pivot, j);
        
        final int x = j;
        //HERE COMES THE FUN!!!!
        if (cores > 1)
        {
            Thread thr1 =  new Thread(){
                public void run()
                {

                    quickSort(a, start, x-1, cores/2);
                }
            };
            
            Thread thr2 = new Thread(){
                public void run()
                {
                    quickSort(a, x + 1, end, cores/2);
                    
                }
            };
            
            thr1.start();
            thr2.start();
            try {
                thr1.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                thr2.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            //just do regular recursive call;
            quickSort(a, start, x-1, cores/2);
            quickSort(a, x + 1, end, cores/2);
        }
    }
    
    private static <T> void swap(T [] a, int i, int j)
    {
        T tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
    
    //used to shuffle input array to quicksort.
    private static void shuffle(Comparable [] a)
    {
        Random random = new Random();
        int index = 0;
        int swap;
        int max = a.length;
        
        Comparable tmp;
        
        do{
            swap = random.nextInt(max) + index;
            tmp = a[index];
            a[index] = a[swap];
            a[swap] = tmp;
            index++;
            max--;
        } while (index < a.length);
        
    }
    
    
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        Integer a[] = {5, 6, 7, 8, 9 ,44, 22, 11, 23, 31, 8, 6, 8, 9, 10, 11, -1, 67, 88, -2, -5, 100};
        
        System.out.print("Before Sort:");
        for (Integer i: a)
        {
            System.out.print(" " + i);
        }
        System.out.println();
        
        quickSort(a);
        System.out.println();
        System.out.print("After Sort:");
        for (Integer i: a)
        {
            System.out.print(" " + i);
        }    }

}
