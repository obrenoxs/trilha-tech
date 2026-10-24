import { LoadingRegion, SkeletonBar, TrailCardSkeleton } from "@/components/ui/skeletons";

// Mesma estrutura da página da área: trilha de navegação, título, descrição e a lista de trilhas.
export default function Loading() {
  return (
    <div className="mx-auto w-full max-w-[1280px] px-5 md:px-10 lg:px-24">
      <LoadingRegion>
        <div className="flex flex-col items-start gap-5 pt-3 pb-12 md:gap-[26px] md:pt-8 md:pb-[72px]">
          <SkeletonBar className="h-4 w-36" />
          <SkeletonBar className="h-9 w-3/4 max-w-[560px] md:h-[60px]" />
          <SkeletonBar className="h-5 w-full max-w-[720px]" />
          <SkeletonBar className="h-4 w-40" />
          <ul aria-hidden="true" className="flex w-full flex-col gap-5 md:gap-[26px]">
            {Array.from({ length: 4 }, (_, index) => (
              <li key={index}>
                <TrailCardSkeleton index={index} />
              </li>
            ))}
          </ul>
        </div>
      </LoadingRegion>
    </div>
  );
}
