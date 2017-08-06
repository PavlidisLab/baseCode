
# LeastSquaresFitTest.testMatrixWeightedMeanVariance
library(limma)
x<-read.csv(header=T,row.names=1,'lmtest.countdata1.txt',sep='\t')
d<-read.csv(header=T,row.names=1,'lmtest.design1.txt',sep='\t') 
d$genotype=factor(d$genotype, levels=c("WT", "KD1", "KD2"))
des<-model.matrix(~d$genotype)
relevel(des$genotype, "WT")
elist<-voom(x,design=des)
voomweights<-elist$weights


# Sanity check: without weights (but data is normalized as per voom)
elist$weights<-NA
fit<-lmFit(elist$E,design=des)
coef(fit[1,])
f1<-lm(elist$E[1,] ~ des)
qr.R(f1$qr)
f2<-lm(elist$E[1,] ~ des )
sqrt(mean(effects(f2)[-c(1,2,3)]^2)) # sigma
effects(f2)


# With weights. Three different ways to the same result.
fit<-lmFit(elist$E,design=des,weights=elist$weights)
f1<-lm.wfit(x=des,y=elist$E[1,],w=elist$weights[1,])
f1<-lm(elist$E[1,] ~ des, weights = elist$weights[1,])

coef(fit[1,])

# Our code gives *slightly* different weights (different lowess impl etc) 
ourweights<-read.csv(header=T, row.names=1, 'lmtest.weights1.txt', sep='\t')
elist$weights = as.matrix(ourweights)
fit<-lmFit(elist$E,design=des,weights=elist$weights)
fit<-noBayes(fit)
# drilling into one example.
anova(f1)
anova(f1)[1,5]
coef(fit[1,])
fit[1,]$sigma
fit[1,]$cov.coefficients
fit[1,]$stdev.unscaled
# digging in
f1<-lm(elist$E[1,] ~ des, weights = elist$weights[1,])
sqrt(mean(effects(f1)[-c(1,2,3)]^2)) # sigma
qr.R(f1$qr)
qr.R(qr(des * sqrt(elist$weights[1,])))
effects(f1)
anova(f1)
summary(f1)
summary(f1)$sigma


