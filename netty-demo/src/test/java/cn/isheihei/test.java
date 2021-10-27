package cn.isheihei;

public class test {
    public static void main(String[] args) {
        System.out.println(isStraight(new int[]{0,0,2,2,5}));
    }
    public static boolean isStraight(int[] nums) {
        sort(nums, 0, nums.length - 1);
        for (int i = 0; i < nums.length; i++) {
            System.out.println(nums[i]);
        }
        int anything = 0;
        int order = 0;
        for(int i = 0; i < nums.length && anything >= 0; i++){
            if(nums[i] == 0){
                anything++;
            }else if(order == 0){
                order = nums[i];
            }else {
                if(order+1 != nums[i]){
                    anything = anything - (nums[i] - order);
                    if (anything < 0)   return false;
                }
                order = nums[i];
            }
        }
        return true;
    }

    public static void sort(int nums[], int left, int right){
        if(left >= right)    return;
        int i = left, j = right;
        int temp = nums[i];

        while(i < j){
            while(i < j && nums[i] <= nums[left]) i++;
            while(i < j && nums[j] >= nums[left]) j--;
            temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
        nums[i] = nums[left];
        nums[left] = temp;
        sort(nums, left, i - 1);
        sort(nums, i + 1, right);
    }
}
