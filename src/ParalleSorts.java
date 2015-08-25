import java.util.Arrays;
import java.util.Random;

public class ParalleSorts {
    
    public static <T extends Comparable<T>> void mergeSort(T [] a)
    {
        //ugly cast needed due to inability to create generic array.
        @SuppressWarnings("unchecked")
        T [] tmp = (T[])new Comparable[a.length]; 
        
        for (int i = 0; i < a.length; i++)
        {
            tmp[i] = a[i];
        }
        int cores = Runtime.getRuntime().availableProcessors();
        mergeSort(a, tmp, 0, a.length - 1, cores);
    }
    
    private static <T extends Comparable<T>> void 
    mergeSort(T [] a, T [] tmp, int start, int end, int cores)
    {
        if (start >= end)
            return;
        
        int mid = (end - start) /2 + start;
        
        
        if (cores  > 1)
        {
            Thread th1 = new Thread(new MergeSortThread<T>(tmp, a, start, mid, cores/2));
            Thread th2 = new Thread(new MergeSortThread<T>(tmp, a, mid + 1, end, cores/2));
            th1.start();
            th2.start();
            try {
                th1.join();
                th2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }            
        }
        else
        {
            mergeSort(tmp, a, start, mid, cores/2);
            mergeSort(tmp, a, mid+1, end, cores/2);
        }
        
        merge(tmp, a, start, mid, end);
    }
    
    private static <T extends Comparable<T>> void merge(T[] src, T[] target, int start, int mid, int end)
    {
        //merges from src array into target array;
        
        int i = start;
        int j = mid+1;
        
        while (i < mid + 1 && j < end+1)
        {
            if(src[i].compareTo(src[j]) < 0)
            {
                target[start++] = src[i++];
            }
            else
            {
                target[start++] = src[j++];
            }
        }
        
        while (i < mid + 1 )
        {
            target[start++] = src[i++];
        }
        
        while (j < end + 1)
        {
            target[start++] = src[j++];
        }
    }
    
    private static class MergeSortThread<T extends Comparable<T>> implements Runnable
    {
        T [] a;
        T [] tmp;
        int start;
        int end;
        int cores;
        
        MergeSortThread(T[] a, T[] tmp, int start, int end, int cores)
        {
            this.a = a;
            this.tmp = tmp;
            this.start = start;
            this.end = end;
            this.cores = cores;
        }

        @Override
        public void run() {
            
            mergeSort(a, tmp, start, end, cores);
            
        }
        
    }

    public static <T extends Comparable<T>> void quickSort(T [] a)
    {
        //shuffle input array to randomize input.
        //this prevents worse case scenario where the array is already sorted
        shuffle(a);
        int cores = Runtime.getRuntime().availableProcessors();
        
        quickSort(a,0,a.length-1, cores);        
    }
    
    private static <T extends Comparable<T>> int split(T[] a, int start, int end)
    {
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
        return j;
    }
    
    private static <T extends Comparable<T>>void quickSort(T [] a, int start, int end, int cores)
    {
        if(start >= end)
            return;
        
        final int x = split(a, start, end);
        //HERE COMES THE FUN!!!!
        if (cores > 1)
        {
            //Anonymous class implementation of quick sort thread. 
            Thread thr1 =  new Thread(){
                public void run()
                {

                    quickSort(a, start, x-1, 1);
                }
            };
            
            //different way of doing the above
            Thread thr2 = new Thread(new QuickSortThread<T>(a, x + 1, end, 1));
              
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
    private static <T extends Comparable<T>> void shuffle(T [] a)
    {
        Random random = new Random();
        int index = 0;
        int swap;
        int max = a.length;
        
        T tmp;
        
        do{
            swap = random.nextInt(max) + index;
            tmp = a[index];
            a[index] = a[swap];
            a[swap] = tmp;
            index++;
            max--;
        } while (index < a.length);
        
        
        
    }
    
    private static class QuickSortThread<T extends Comparable<T>> implements Runnable {

            T []a;
            int start;
            int end;
            int cores;
            
            QuickSortThread(T [] a, int start, int end, int cores)
            {
                this.a = a;
                this.start = start;
                this.end = end;
                this.cores = cores;
            }
            
            public void run() {
                // Run static method quick sort defined in outer class;
                quickSort(a, start, end, cores);
            }
            
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        Integer a[] = {5, 6, 7, 8, 9 ,44, 22, 11, 23, 31, 8, 6, 8, 9, 10, 11, -1, 67, 88, -2, -5, 100};
        
        Integer b[] = Arrays.copyOf(a, a.length);
        
        System.out.print("Before Sort:");
        for (Integer i: b)
        {
            System.out.print(" " + i);
        }
        System.out.println();
        
        quickSort(a);
        System.out.println();
        System.out.print("After Quick Sort:");
        for (Integer i: a)
        {
            System.out.print(" " + i);
        }
        System.out.println();
        
        mergeSort(b);
        System.out.print("After Merge Sort:");
        for (Integer i: b)
        {
            System.out.print(" " + i);
        }      
    }

}
