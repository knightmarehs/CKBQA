package utils;

public class EditDistance 
{
	public static int calcutateEditDistance(String s1, String s2)
	{
		int n = s1.length(), m = s2.length();
		int[][] dp = new int[n+5][m+5];
		for (int i=0;i<=n;i++) dp[i][0] = i;
		for (int j=0;j<=m;j++) dp[0][j] = j;
		for (int i=1;i<=n;i++)
			for (int j=1;j<=m;j++)
			{
				int x = s1.codePointAt(i-1) == s2.codePointAt(j-1) ? 0 : 1;
				dp[i][j] = Math.min(dp[i][j-1], dp[i-1][j]) + 1;
				dp[i][j] = Math.min(dp[i][j], dp[i-1][j-1]+x);
			}
		return dp[n][m];
	}

}
